package qengineRendu.program;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import qengineRendu.program.parser.Parser;
import qengineRendu.program.parser.QueryParser;
import qengineRendu.program.service.IDictionaryIndexesService;
import qengineRendu.program.service.impl.DictionaryIndexesServiceImpl;
import qengineRendu.program.utils.FilePath;
import qengineRendu.program.utils.StatisticQuery;
import qengineRendu.program.utils.TypeIndex;

import java.io.*;


public class MainTest {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(MainTest.class);
    public static void main(String[] args) throws Exception {
        Options options = new Options();

        Option queries = new Option("q", "queries", true, "chemin vers dossier requetes");
        queries.setRequired(true);
        options.addOption(queries);

        Option data = new Option("d", "data", true, "chemin vers fichier donnees");
        data.setRequired(true);
        options.addOption(data);

        Option output = new Option("o", "output", true, "chemin vers dossier sortie");
        output.setRequired(true);
        options.addOption(output);

        Option jenaActivation = new Option("j", "jena", true, "active la vérification de la correction et complétude du système\n" +
                "en utilisant Jena comme un oracle");
        jenaActivation.setRequired(true);
        options.addOption(jenaActivation);

        Option warm = new Option("w", "warm", true, "utilise un échantillon des requêtes en entrée (prises\n" +
                "au hasard) correspondant au pourcentage X pour chauffer le système");
        warm.setRequired(false);
        options.addOption(warm);

        Option shuffle = new Option("s", "shuffle", true, " considère une permutation aléatoire des requêtes en entrée");
        shuffle.setRequired(false);
        options.addOption(shuffle);

        CommandLineParser parserCli = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;//not a good practice, it serves it purpose

        try {
            cmd = parserCli.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }

        String dataFilePath = cmd.getOptionValue("data");
        String outputFilePath = cmd.getOptionValue("output");
        String queriesFilePath = cmd.getOptionValue("queries");
        String jenaActivationOption = cmd.getOptionValue("jena");
        String warmOption = cmd.getOptionValue("warm");
        String shuffleOption = cmd.getOptionValue("shuffle");
        logger.info("dataFilePath: {}", dataFilePath);
        logger.info("outputFilePath: {}", outputFilePath);
        logger.info("queriesFilePath: {}", queriesFilePath);
        logger.info("jenaActivationOption: {}", jenaActivationOption);
        logger.info("warmOption: {}", warmOption);
        logger.info("shuffleOption: {}", shuffleOption);


        FilePath fileManagement = new FilePath(queriesFilePath, dataFilePath, outputFilePath);
        Parser parser = new Parser(fileManagement);
        parser.parse();
        QueryParser queryParser = new QueryParser(fileManagement);
        queryParser.readQueries();
        if (shuffleOption != null && shuffleOption.equals("true")) {
            logger.info("Shuffle option activated");
            queryParser.shuffelQueries();
        }
        if (warmOption != null && !warmOption.isEmpty()) {
            logger.info("Warming up the system");
            queryParser.warmUpQueries(Integer.parseInt(warmOption));
        }

        if (jenaActivationOption.equals("true")) {
            queryParser.parse(2);
        } else {
            queryParser.parse(1);
        }

        try (Writer outputFile = new BufferedWriter(new FileWriter(fileManagement.getOutputFolder() + File.separator + "file.txt", false))) {
            StatisticQuery.getStatisticQueries().stream().map(StatisticQuery::toString).forEach(s -> {
                try {
                    outputFile.write(s);
                    outputFile.write("\n\n\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.info(" le temps total d’évaluation du workload est de {} ms", StatisticQuery.getTotalTimeExecution());

    }

}
