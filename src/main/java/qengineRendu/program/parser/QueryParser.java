package qengineRendu.program.parser;

import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.helpers.StatementPatternCollector;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;
import org.slf4j.Logger;
import qengineRendu.program.service.IDictionaryIndexesService;
import qengineRendu.program.service.impl.DictionaryIndexesServiceImpl;
import qengineRendu.program.utils.Dictionary;
import qengineRendu.program.utils.FilePath;
import qengineRendu.program.utils.StatisticQuery;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Stream;

public class QueryParser {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(QueryParser.class);
    IDictionaryIndexesService dictionaryService = new DictionaryIndexesServiceImpl();
    private  JenaParser jenaParser;
    private final FilePath fileManagement;
    private static final SPARQLParser sparqlParser = new SPARQLParser();
    private List<String> queriesDictionary = new ArrayList<>();

    public QueryParser(FilePath fileManagement) {
        this.fileManagement = fileManagement;
        jenaParser = new JenaParser(fileManagement.getDataFile());
    }

    public void myParser(String queryString, String baseURI){
        ParsedQuery query = sparqlParser.parseQuery(queryString, baseURI);
        List<String[]> queryResult = processAQuery(query); // Traitement de la requête, à adapter/réécrire pour votre programme
        Set<Long> queryExecutionResult = new HashSet<>();
        // Parcourir les requêtes et trouver les résultats
        for(int j=0; j<queryResult.size(); j++){
            dictionaryService.searchFromDictionaryByIndexesObjects(queryResult.get(j), queryExecutionResult, j==0);
            if (queryExecutionResult.isEmpty()) break;
        }
    }

    public void jenaParse(List<String> queries) throws Exception {
        for (int j=0; j<queries.size(); j++) {
            Long startTime = System.nanoTime();
            jenaParser.jenaParser(queries.get(j));
            Long endTime = System.nanoTime();
            new StatisticQuery(queries.get(j), j+1, endTime - startTime);
            logger.info("Query {} parsed in {} ms", j+1, ((endTime - startTime) / 1_000_000.0));
        }

    }

    public List<String[]> processAQuery(ParsedQuery query) {
        List<StatementPattern> patterns = StatementPatternCollector.process(query.getTupleExpr());
        List<String[]> queryResult = new ArrayList<>();
        for(StatementPattern pattern : patterns){
           String[] patternResult = new String[3];
            patternResult[0] = (pattern.getSubjectVar().getValue() != null ? pattern.getSubjectVar().getValue().stringValue() : null);
            patternResult[1] = (pattern.getPredicateVar().getValue() != null ? pattern.getPredicateVar().getValue().stringValue() : null);
            patternResult[2] = (String.valueOf(pattern.getObjectVar().getValue() != null ? pattern.getObjectVar().getValue().stringValue() : null));
            queryResult.add(patternResult);
        }
        return queryResult;
    }

    public void shuffelQueries() {
        Collections.shuffle(queriesDictionary, new Random());
    }

    public void warmUpQueries(int prc) {
       shuffelQueries();
       int nbQueries = (prc * queriesDictionary.size()) / 100;
       queriesDictionary = new ArrayList<>(queriesDictionary.subList(0, nbQueries));
    }
    public void readQueries() throws FileNotFoundException, IOException {
        try (Stream<String> lineStream = Files.lines(Paths.get(fileManagement.getQueryFile()))) {
            Iterator<String> lineIterator = lineStream.iterator();
            StringBuilder queryString = new StringBuilder();
                while (lineIterator.hasNext())
                {
                    String line = lineIterator.next();
                    queryString.append(line);
                    if (line.trim().endsWith("}")) {
                        queriesDictionary.add(queryString.toString());
                        queryString.setLength(0);
                    }
                }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void parse(int parserNumber) throws Exception {

        switch (parserNumber){
            case 1:
                for(int i=0; i<queriesDictionary.size(); i++){
                    Long startTime = System.nanoTime();
                    myParser(queriesDictionary.get(i), fileManagement.getBasUrl());
                    Long endTime = System.nanoTime();
                    new StatisticQuery(queriesDictionary.get(i), i, endTime - startTime);
                    logger.info("Query {} parsed in {} ms", i+1, ((endTime - startTime) / 1_000_000.0));
                }
                break;
            case 2:
                jenaParse(queriesDictionary);
                break;
            default:
                System.out.println("Please choose a parser");
        }
    }
}
