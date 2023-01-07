package qengineRendu.program.parser.moteurRDF;

import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.helpers.StatementPatternCollector;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;
import org.slf4j.Logger;
import qengineRendu.program.parser.DataParser;
import qengineRendu.program.parser.jenna.JenaParser;
import qengineRendu.program.service.IDictionaryIndexesService;
import qengineRendu.program.service.impl.DictionaryIndexesServiceImpl;
import qengineRendu.program.utils.FilePath;
import qengineRendu.program.utils.StatisticData;
import qengineRendu.program.utils.StatisticQuery;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Query parser.
 */
public class RdfQueryParser {
    /**
     * The constant logger.
     */
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(RdfQueryParser.class);
    /**
     * The Dictionary service.
     */
    IDictionaryIndexesService dictionaryService = new DictionaryIndexesServiceImpl();
    /**
     * The File management.
     */
    private final FilePath fileManagement;
    /**
     * The constant sparqlParser.
     */
    private static final SPARQLParser sparqlParser = new SPARQLParser();

    private JenaParser jenaParser;
    private boolean isJeanna;
    /**
     * The Queries dictionary.
     */
    private final Map<String, List<String>> queriesDictionary;
    private Set<String> queriesResults;
    private final String exportPath;

    /**
     * Instantiates a new Query parser.
     *
     * @param fileManagement the file management
     * @param exportPath
     */
    public RdfQueryParser(FilePath fileManagement, boolean isJenna, String exportPath) {
        this.isJeanna = isJenna;
        this.fileManagement = fileManagement;
        this.exportPath = exportPath;
        long readingQueriesStart = System.nanoTime();
        this.queriesDictionary = new HashMap<>(fileManagement.getFilesQueries());
        long readingQueriesEnd = System.nanoTime();
        StatisticData.timeReadingQueries = (readingQueriesEnd - readingQueriesStart) / 1_000_000.0;
        logger.info("Time to read queries : {} ms", StatisticData.timeReadingQueries);
        if (isJenna) {
            jenaParser = new JenaParser(DataParser.getModel(), this.queriesDictionary);
        }
    }

    /**
     * parse the queries via sparqlParser and search on the dictionary by indexes for the result.
     *
     * @param queryString the query string
     * @param baseURI     the base uri
     */
    public void myParser(String queryString, String baseURI) {
        ParsedQuery query = sparqlParser.parseQuery(queryString, baseURI);
        List<String[]> queryResult = processAQuery(query);
        Set<Long> queryExecutionResult = new HashSet<>();
        for (int j = 0; j < queryResult.size(); j++) {
            dictionaryService.searchFromDictionaryByIndexesObjects(queryResult.get(j), queryExecutionResult, j == 0);
            if (queryExecutionResult.isEmpty()) break;
            if (exportPath != null && !exportPath.isEmpty()) {
                writeResultInCsv(queryExecutionResult, exportPath);
            }

        }
        if (isJeanna) {
            Set<String> jennaResults = jenaParser.doQueryResult(queryString);
            queriesResults = dictionaryService.decodeListOfIndexes(queryExecutionResult);
            compareResultsIfEqual(queriesResults, jennaResults, queryString);
        }
    }

    private void writeResultInCsv(Set<Long> queryExecutionResult, String exportPath) {
        Set<String> queryExecutionResultDecoded = dictionaryService.decodeListOfIndexes(queryExecutionResult);
        try {
            File file = new File(exportPath+ File.separator + "resultQueries.csv");
            FileWriter fileWriter = new FileWriter(file, true);
            String result = String.join(";", queryExecutionResultDecoded);
            fileWriter.write(queryExecutionResult.size());
            fileWriter.write(result + System.lineSeparator());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void compareResultsIfEqual(Set<String> queriesResults, Set<String> jennaResults, String query) {
       if (queriesResults.equals(jennaResults)) {
           StatisticData.correctResults++;
       } else {
           StatisticData.wrongResults++;
           logger.error("Query not equal : {}", query);
       }
    }


    /**
     * Process a query list.
     *
     * @param query the query
     * @return the list
     */
    public List<String[]> processAQuery(ParsedQuery query) {
        List<StatementPattern> patterns = StatementPatternCollector.process(query.getTupleExpr());
        List<String[]> queryResult = new ArrayList<>();
        for (StatementPattern pattern : patterns) {
            String[] patternResult = new String[3];
            patternResult[0] = (pattern.getSubjectVar().getValue() != null ? pattern.getSubjectVar().getValue().stringValue() : null);
            patternResult[1] = (pattern.getPredicateVar().getValue() != null ? pattern.getPredicateVar().getValue().stringValue() : null);
            patternResult[2] = (String.valueOf(pattern.getObjectVar().getValue() != null ? pattern.getObjectVar().getValue().stringValue() : null));
            queryResult.add(patternResult);
        }
        return queryResult;
    }

    /**
     * Shuffle queries.
     */
    public void shuffleQueries() {
        queriesDictionary.forEach((k, v) -> Collections.shuffle(v));
    }

    /**
     * Warm up queries.
     *
     * @param prc the percentage of queries to warm up.
     */
    public void warmUpQueries(int prc) {
        Long startWarmUp = System.nanoTime();
        for (Map.Entry<String, List<String>> entry : queriesDictionary.entrySet()) {
            int percentage = (((prc * entry.getValue().size()) / 100) == 0) ? 1 : (prc * entry.getValue().size()) / 100;
            entry.setValue(entry.getValue().subList(0, percentage));
        }
        Long endWarmUp = System.nanoTime();
        StatisticData.timeWarmUp = (endWarmUp - startWarmUp) / 1_000_000.0;
    }

    /**
     * Parse.
     */
    public void parse() {
        Long startParsing = System.nanoTime();
        for (Map.Entry<String, List<String>> entry : queriesDictionary.entrySet()) {
            logger.info("Processing query file {} of {} queries", entry.getKey(), entry.getValue().size());
            for (int j = 0; j < entry.getValue().size(); j++) {
                Long startTime = System.nanoTime();
                myParser(entry.getValue().get(j), null);
                Long endTime = System.nanoTime();
                new StatisticQuery(entry.getKey(), entry.getValue().get(j), endTime - startTime, j + 1);
                logger.info("Query {} parsed in {} ms", j + 1, ((endTime - startTime) / 1_000_000.0));
            }
            logger.info("le temps total de requêtes évaluées dans le fichier {} est de {} ms", entry.getKey(), StatisticQuery.getTotalTimeExecutionStaticInFile());
            StatisticQuery.getStatisticQueriesInFiles().put(entry.getKey(), new ArrayList<>(StatisticQuery.getStatisticQueriesInFile()));
            StatisticQuery.refreshStatisticQueriesInFile();
        }
        Long endParsing = System.nanoTime();
        double timeParsing = (endParsing - startParsing) / 1_000_000.0;
        StatisticData.timeTotalParsingQueries = timeParsing;
        logger.info("le temps total de requêtes évaluées rdf parser est de {} ms", timeParsing);
    }
}
