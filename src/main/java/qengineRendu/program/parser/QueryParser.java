package qengineRendu.program.parser;

import org.eclipse.rdf4j.query.algebra.Projection;
import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.helpers.AbstractQueryModelVisitor;
import org.eclipse.rdf4j.query.algebra.helpers.StatementPatternCollector;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;
import qengineRendu.program.service.IDictionaryIndexesService;
import qengineRendu.program.service.impl.DictionaryIndexesServiceImpl;
import qengineRendu.program.utils.Dictionary;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class QueryParser {
    IDictionaryIndexesService dictionaryService = new DictionaryIndexesServiceImpl();

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

            System.out.println("subject1 : " + patternResult.get(0));
            System.out.println("predicate : " + patternResult.get(1));
            System.out.println("object : " + patternResult.get(2));

//            System.out.println("Subject: "+pattern.getSubjectVar());
//            System.out.println("Predicat: "+pattern.getPredicateVar());
//            System.out.println("Object :"+pattern.getObjectVar());
        }

        // Utilisation d'une classe anonyme
        query.getTupleExpr().visit(new AbstractQueryModelVisitor<RuntimeException>() {
            public void meet(Projection projection) {
                System.out.println("Project Element: "+projection.getProjectionElemList().getElements());
            }
        });
        return queryResult;
    }

    // make it private
    public void parse(String baseURI, String queryFile) throws FileNotFoundException, IOException {
        /**
         * Try-with-resources
         *
         * @see <a href="https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html">Try-with-resources</a>
         */
        /*
         * On utilise un stream pour lire les lignes une par une, sans avoir à toutes les stocker
         * entièrement dans une collection.
         */
        try (Stream<String> lineStream = Files.lines(Paths.get(queryFile))) {
            SPARQLParser sparqlParser = new SPARQLParser();
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
                    ParsedQuery query = sparqlParser.parseQuery(queryString.toString(), baseURI);
                    List<List<String>> queryResult = processAQuery(query); // Traitement de la requête, à adapter/réécrire pour votre programme
                    System.out.println("============ Query " + i + " ============");
                    // Parcourir les requêtes et trouver les résultats
                    for(List<String> pattern : queryResult){
                        System.out.println(dictionaryService.searchFromDictionaryByIndexesObjects(pattern));
                    }
                    queryString.setLength(0); // Reset le buffer de la requête en chaine vide
                }
            }
        }
    }
}
