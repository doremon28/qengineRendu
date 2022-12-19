package qengineRendu.program.parser;

import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.helpers.StatementPatternCollector;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;
import org.slf4j.Logger;
import qengineRendu.program.service.IDictionaryIndexesService;
import qengineRendu.program.service.impl.DictionaryIndexesServiceImpl;
import qengineRendu.program.utils.FilePath;
import qengineRendu.program.utils.StatisticData;
import qengineRendu.program.utils.StatisticQuery;

import java.util.*;


/**
 * Query parser.
 */
public class QueryParser {
    /**
     * The constant logger.
     */
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(QueryParser.class);
    /**
     * The Dictionary service.
     */
    IDictionaryIndexesService dictionaryService = new DictionaryIndexesServiceImpl();
    /**
     * The Jena parser.
     */
    private final JenaParser jenaParser;
    /**
     * The File management.
     */
    private final FilePath fileManagement;
    /**
     * The constant sparqlParser.
     */
    private static final SPARQLParser sparqlParser = new SPARQLParser();
    /**
     * The Queries dictionary.
     */
    private final Map<String, List<String>> queriesDictionary;

    /**
     * Instantiates a new Query parser.
     *
     * @param fileManagement the file management
     */
    public QueryParser(FilePath fileManagement) {
        this.fileManagement = fileManagement;
        long readingQueriesStart = System.nanoTime();
        // creation of the query dictionary
        this.queriesDictionary = new HashMap<>(fileManagement.getFilesQueries());
        long readingQueriesEnd = System.nanoTime();
        StatisticData.timeReadingQueries = (readingQueriesEnd - readingQueriesStart) / 1_000_000.0;
        logger.info("Time to read queries : {} ms", StatisticData.timeReadingQueries);
        jenaParser = new JenaParser(fileManagement.getDataFile());
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
        }
    }

    /**
     * Jena parse.
     *
     * @param fileName the file name
     * @param queries  the queries
     * @throws Exception the exception
     */
    public void jenaParse(String fileName, List<String> queries) throws Exception {
        for (int j = 0; j < queries.size(); j++) {
            Long startTime = System.nanoTime();
            jenaParser.jenaParser(queries.get(j));
            Long endTime = System.nanoTime();
            new StatisticQuery(fileName, queries.get(j), endTime - startTime, j+1);
            logger.info("Query {} parsed in {} ms", j + 1, ((endTime - startTime) / 1_000_000.0));
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
        for (Map.Entry<String, List<String>> entry : queriesDictionary.entrySet()) {
            int percentage = (((prc * entry.getValue().size()) / 100) == 0) ? 1 : (prc * entry.getValue().size()) / 100;
            entry.setValue(entry.getValue().subList(0, percentage));
        }

    }

    /**
     * Parse.
     *
     * @param parserNumber the parser number
     * @throws Exception the exception
     */
    public void parse(int parserNumber) throws Exception {
        switch (parserNumber) {
            case 1:
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
                break;
            case 2:
                for (Map.Entry<String, List<String>> entry : queriesDictionary.entrySet()) {
                    logger.info("Processing query file {} of {} queries", entry.getKey(), entry.getValue().size());
                    jenaParse(entry.getKey(), entry.getValue());
                }
                break;
            default:
                System.out.println("Please choose a parser");
        }
    }
}
