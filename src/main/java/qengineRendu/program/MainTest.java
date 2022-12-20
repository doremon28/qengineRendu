package qengineRendu.program;

import org.apache.commons.cli.*;
import org.slf4j.Logger;
import qengineRendu.program.parser.Parser;
import qengineRendu.program.parser.QueryParser;
import qengineRendu.program.service.IDictionaryIndexesService;
import qengineRendu.program.service.impl.DictionaryIndexesServiceImpl;
import qengineRendu.program.utils.*;

import java.io.*;


public class MainTest {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(MainTest.class);

    public static void main(String[] args) throws Exception {
        long startTimeWorkload = System.nanoTime();
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
        if (shuffleOption != null && shuffleOption.equals("true")) {
            logger.info("Shuffle option activated");
            queryParser.shuffleQueries();
        }
        if (warmOption != null && !warmOption.isEmpty()) {
            logger.info("Warming up the system");
            queryParser.warmUpQueries(Integer.parseInt(warmOption));
        }
        if (jenaActivationOption.equals("true")) {
            logger.info("Jena activation option activated");
            queryParser.parse(2);
        } else {
            logger.info("Jena activation option deactivated");
            queryParser.parse(1);
        }
        logger.info(" le nombre total de requêtes évaluées est de {}", StatisticQuery.getTotalTimeExecutionInFiles());
        long endTimeWorkload = System.nanoTime();
        StatisticData.timeWorkload = (endTimeWorkload - startTimeWorkload) / 1_000_000.0;
        logger.info(" le temps total d’évaluation du workload est de {} ms", StatisticData.timeWorkload);
        if (outputFilePath != null && !outputFilePath.isEmpty()) {
            fileManagement.generateFile(2, jenaActivationOption.equals("true"));
        }
    }

}
