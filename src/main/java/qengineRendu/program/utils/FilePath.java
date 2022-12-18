package qengineRendu.program.utils;

import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;

public class FilePath {

    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(FilePath.class);
    private final String basUrl;
    private final String queryDir;
    private final String dataFile;
    private final String outputFolder;

    public FilePath(String queryDir, String dataFile, String outputFolder) {
        this.basUrl = null;
        this.queryDir = queryDir;
        this.dataFile = dataFile;
        this.outputFolder = outputFolder;
    }

    public String getBasUrl() {
        return basUrl;
    }

    public String getQueryDir() {
        return queryDir;
    }

    public String getDataFile() {
        return dataFile;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    private void generateGeneraleInformationCsv(String filePath) throws IOException {
        FileWriter outputfile = new FileWriter(filePath);
        CSVWriter writer = new CSVWriter(outputfile);
        int nbQueries = StatisticQuery.getTotalQueryNumberInFiles();
        double timeToEvaluateQueries = StatisticQuery.getTotalTimeExecutionInFiles();
        int nbTriples = 3;
        String[] header = {"fichier_données", "dossier_requêtes", "nombre_triplets_RDF", "nombre_requêtes", "temps_lecture_données", "temps_lecture_requêtes", "temps_creation_dico", "temps_creation_index", "temps_total_evaluation", "temps_total",};
        String[] data = {getDataFile(), getQueryDir(), String.valueOf(nbTriples), String.valueOf(nbQueries), StatisticData.timeReadingData + " ms", StatisticData.timeReadingQueries + " ms", StatisticData.creatingDictionary + " ms", StatisticData.creatingIndexes + " ms", timeToEvaluateQueries+" ms", StatisticData.timeWorkload + " ms"};
        writer.writeNext(header);
        writer.writeNext(data);
        writer.close();

    }

    /**
     * Generate 2 files csv statistics.
     * file 1 : (general_information.csv) general information about the workload
     * file 2 : (fileStatistics.csv) statistics about each query
     * @throws IOException the io exception
     */
    private void generateFileCsvStatistics() throws IOException {
        logger.info("Generating CSV file for general information................................");
        logger.info("Generale file generated");
        try (Writer outputFile = new BufferedWriter(new FileWriter(this.outputFolder + File.separator + "fileStatistics.csv", false))) {
            logger.info("Generating fileStatistics.csv..........................");
            StatisticQuery
                    .getStatisticsQueriesCsv()
                    .stream()
                    .map(s -> String.join(";", s))
                    .forEach(s -> {
                        try {
                            outputFile.write(s);
                            outputFile.write("\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                            logger.error("Error while writing file csv statistics");
                        }
                    });
            logger.info("File statistics generated");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Error while writing file statistics");
        }
    }

    private void generateFileTxtStatistics() {
        try (Writer outputFile = new BufferedWriter(new FileWriter(this.outputFolder + File.separator + "fileStatistics.txt", false))) {
            StatisticQuery.getStatisticQueriesInFile().forEach(s -> {
                try {
                    outputFile.write(s.toString());
                    outputFile.write("\n");
                } catch (IOException e) {
                    e.printStackTrace();
                    logger.error("Error while writing file txt statistics");
                }
            });
            logger.info("File statistics generated");
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Error while writing file statistics");
        }
    }

    public void generateFile(int choice) throws IOException {
        if (choice == 1) {
            generateFileTxtStatistics();
        } else if (choice == 2) {
            generateFileCsvStatistics();
            generateGeneraleInformationCsv(getOutputFolder() + "/general_information.csv");
        }
    }

    public void repairFileQueriesFormat(String filePath){
        // Read file
        Path pathFile = Paths.get(filePath);
        try (Stream<String> fileLines = Files.lines(pathFile)) {
            List<String> lines = fileLines.collect(Collectors.toList());
            AtomicBoolean queryIsCorrect = new AtomicBoolean(false);
            // Check if the query is closed by accolade
            lines.forEach(line -> {
                if(line.contains("}")){
                    queryIsCorrect.set(true);
                }
            });
            // If the query is not closed by accolade, we add it
            if(queryIsCorrect.get()){
                logger.info("The file {} is already in the correct format", pathFile.getFileName());
            }else{
                for(int i=0; i<lines.size()-1; i++){
                    String line = lines.get(i);
                    if(lines.get(i+1).contains("SELECT")){
                        lines.set(i, line+"}\n");
                    }
                }
                logger.info("The file {} is now in the correct format", pathFile.getFileName());
            }
            Files.write(pathFile, lines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, List<String>> getFilesQueries() {

        if (this.queryDir == null) {
            return Collections.emptyMap();
        }
        return handleFolderFilesQueries(this.queryDir);
    }

    private Map<String, List<String>> handleFolderFilesQueries(String folderPath) {
        long readingQueriesStart = System.nanoTime();
        File folder = new File(folderPath);
        List<File> listOfFiles;
        if (folder.isDirectory()) {
            listOfFiles = Arrays.stream(Objects.requireNonNull(folder.listFiles())).collect(Collectors.toList());
            if (!listOfFiles.isEmpty()) {
                List<File> listOfValidQueriesFiles = listOfFiles.stream().filter(file -> file.getName().endsWith(".queryset")).collect(Collectors.toList());
                return listOfValidQueriesFiles.stream().collect(Collectors.toMap(File::getName, file -> handleFileQueries(file.getAbsolutePath())));
            } else {
                logger.error("No files in folder {}", folderPath);
                System.exit(1);
            }
            long readingQueriesEnd = System.nanoTime();
            StatisticData.timeReadingQueries = (readingQueriesEnd - readingQueriesStart) / 1_000_000.0;
            logger.info("Time to read queries : {} ms", StatisticData.timeReadingQueries);
        } else {
            logger.error("Folder {} is not a directory", folderPath);
            System.exit(1);
        }

        return Collections.emptyMap();
    }

    private List<String> handleFileQueries(String filePath) {
        StringBuilder contentBuilder = new StringBuilder();
        List<String> list = new ArrayList<>();
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            stream.forEach(s -> {
                contentBuilder.append(s);
                if (s.trim().endsWith("}")) {
                    list.add(contentBuilder.toString());
                    contentBuilder.setLength(0);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Error while reading file {}", filePath);
        }
        return list;
    }
}
