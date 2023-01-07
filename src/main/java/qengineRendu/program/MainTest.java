package qengineRendu.program;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import qengineRendu.program.parser.DataParser;
import qengineRendu.program.parser.moteurRDF.RdfQueryParser;
import qengineRendu.program.utils.*;
import java.io.IOException;


public class MainTest {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(MainTest.class);
    private static String dataFilePath;
    private static String outputFilePath;
    private static String queriesFilePath;
    private static String jenaActivationOption;
    private static String warmOption;
    private static String shuffleOption;

    private static String exportPath = null;

    public static void main(String[] args) {
        Long startTime = System.currentTimeMillis();
        OptionsCli optionsCli = new OptionsCli();
        CommandLineParser parserCli = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;
        Options options = optionsCli.getOptions();
        try {
            cmd = parserCli.parse(options, args);
        } catch (ParseException e) {
            logger.error(e.getMessage());
            formatter.printHelp("utility-name", options);
            System.exit(1);
        }
        dataFilePath = cmd.getOptionValue("data");
        outputFilePath = cmd.getOptionValue("output");
        queriesFilePath = cmd.getOptionValue("queries");
        jenaActivationOption = cmd.getOptionValue("jena");
        warmOption = cmd.getOptionValue("warm");
        shuffleOption = cmd.getOptionValue("shuffle");
        exportPath = cmd.getOptionValue("exportQueryResult");
        logger.info("dataFilePath: {}", dataFilePath);
        logger.info("outputFilePath: {}", outputFilePath);
        logger.info("queriesFilePath: {}", queriesFilePath);
        logger.info("jenaActivationOption: {}", jenaActivationOption);
        logger.info("warmOption: {}", warmOption);
        logger.info("shuffleOption: {}", shuffleOption);
        logger.info("exportPath: {}", exportPath);
        FilePath fileManagement = new FilePath(queriesFilePath, dataFilePath, outputFilePath);
        DataParser dataParser = new DataParser(fileManagement);
        boolean isJenna = jenaActivationOption != null && !jenaActivationOption.isEmpty();
        try {
            dataParser.parse(isJenna);
        } catch (IOException e) {
            logger.error("Error message while parsing data {}", e.getMessage());
        }

        RdfQueryParser queryParser = new RdfQueryParser(fileManagement, isJenna, exportPath);
        rdfParserLauncher(queryParser);

        logger.info(" le temps total de requêtes évaluées est de {}", StatisticQuery.getTotalTimeExecutionInFiles());


        logger.info(" le temps total d’évaluation du workload est de {} ms", StatisticData.timeWorkload);
        if (outputFilePath != null && !outputFilePath.isEmpty()) {
            try {
                Long endTime = System.currentTimeMillis();
                StatisticData.timeTotalExcecution = (endTime - startTime) == 0 ? 1 : (endTime - startTime);
                fileManagement.generateFile(2);
            } catch (IOException e) {
                logger.error("Error message while generating file {}", e.getMessage());
            }
        }
        if (isJenna) {
            showVerificationResult();
        }

        logger.info("le temps de warmup est de {} ms", StatisticData.timeWarmUp);
    }

    private static void showVerificationResult() {
        logger.info("Le nombre des requetes avec des résultat égale a ceux de Jenna est {}", StatisticData.correctResults);
        logger.info("Le nombre des requetes avec des résultat différent de ceux de Jenna est {}", StatisticData.wrongResults);
        if (StatisticData.wrongResults > 0) {
            logger.error("La vérification de la correction et de la complétude n’est pas passé");
        } else {
            logger.warn("La vérification de la correction et de la complétude est passé");
        }
    }

    private static void rdfParserLauncher(RdfQueryParser queryParser) {
        if (isShuffleOption(shuffleOption)) {
            logger.info("Shuffle option activated");
            queryParser.shuffleQueries();
        }
        if (isWarmOption(warmOption)) {
            logger.info("Warming up the system");
            queryParser.warmUpQueries(Integer.parseInt(warmOption));
        }
        queryParser.parse();
    }

    private static boolean isShuffleOption(String shuffleOption) {
        return shuffleOption != null && shuffleOption.equals("true");
    }

    private static boolean isWarmOption(String warmOption) {
        return warmOption != null && !warmOption.isEmpty();
    }

}
