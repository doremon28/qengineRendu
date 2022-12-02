package qengineRendu.program.parser;

import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.helpers.StatementPatternCollector;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;
import qengineRendu.program.service.IDictionaryIndexesService;
import qengineRendu.program.service.impl.DictionaryIndexesServiceImpl;
import qengineRendu.program.utils.Dictionary;
import qengineRendu.program.utils.FilePath;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class QueryParser {
    IDictionaryIndexesService dictionaryService = new DictionaryIndexesServiceImpl();
    private JenaParser jenaParser = new JenaParser();
    private final FilePath fileManagement;
    private static SPARQLParser sparqlParser = new SPARQLParser();

    public QueryParser(FilePath fileManagement) {
        this.fileManagement = fileManagement;
    }

    public List<String> myParser(String queryString, String baseURI){
        List<String> queryResults = new ArrayList<>();
        ParsedQuery query = sparqlParser.parseQuery(queryString.toString(), baseURI);
        List<List<String>> queryResult = processAQuery(query); // Traitement de la requête, à adapter/réécrire pour votre programme
        HashSet<Long> queryExecutionResult = new HashSet<>();
        // Parcourir les requêtes et trouver les résultats
        for(int j=0; j<queryResult.size(); j++){
            dictionaryService.searchFromDictionaryByIndexesObjects(queryResult.get(j), queryExecutionResult, j==0);
            if (queryExecutionResult.isEmpty()) break;
        }

        qengineRendu.program.utils.Dictionary dictionary = new Dictionary();
        return dictionary.decodeList(queryExecutionResult);
    }

    public List<String> jenaParse(String queryString, String baseURI) throws Exception {
        return jenaParser.jenaParser(queryString, baseURI);

    }

    public List<List<String>> processAQuery(ParsedQuery query) {
        List<StatementPattern> patterns = StatementPatternCollector.process(query.getTupleExpr());
        List<List<String>> queryResult = new ArrayList<>();
        for(StatementPattern pattern : patterns){
            //
            List<String> patternResult = new ArrayList<>();
            patternResult.add(pattern.getSubjectVar().getValue() != null ? pattern.getSubjectVar().getValue().stringValue() : null);
            patternResult.add(pattern.getPredicateVar().getValue() != null ? pattern.getPredicateVar().getValue().stringValue() : null);
            patternResult.add(String.valueOf(pattern.getObjectVar().getValue() != null ? pattern.getObjectVar().getValue().stringValue() : null));
            queryResult.add(patternResult);
        }
        return queryResult;
    }


    public void parse(int parserNumber) throws FileNotFoundException, IOException {
        /**
         * Try-with-resources
         *
         * @see <a href="https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html">Try-with-resources</a>
         */
        /*
         * On utilise un stream pour lire les lignes une par une, sans avoir à toutes les stocker
         * entièrement dans une collection.
         */
        try (Stream<String> lineStream = Files.lines(Paths.get(fileManagement.getQueryFile()))) {

            Iterator<String> lineIterator = lineStream.iterator();
            StringBuilder queryString = new StringBuilder();
            int i = 1;
            while (lineIterator.hasNext())
                /*
                 * On stocke plusieurs lignes jusqu'à ce que l'une d'entre elles se termine par un '}'
                 * On considère alors que c'est la fin d'une requête
                 */
            {
                String line = lineIterator.next();
                queryString.append(line);
                if (line.trim().endsWith("}")) {

                    switch (parserNumber){
                        case 1:
                            System.out.println("\n============ Our Parser: Query:"+ i+"============");
                            for(String result : myParser(queryString.toString(), fileManagement.getBasUrl())){
                                System.out.println(result);
                            }
                            i++;
                            break;
                        case 2:
                            System.out.println("\n============ Jena Parser: Query:"+i+" ============");
                            for(String result : jenaParse(fileManagement.getDataFile(), queryString.toString())){
                                System.out.println(result);
                            }
                            i++;
                            break;
                        default:
                            System.out.println("Please choose a parser");
                    }

                    queryString.setLength(0); // Reset le buffer de la requête en chaine vide

                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
