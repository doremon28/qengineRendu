package qengineRendu.program.parser.jenna;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.slf4j.Logger;
import qengineRendu.program.utils.FilePath;

import java.util.*;

/**
 * Jena parser.
 */
public class JenaParser {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(JenaParser.class);

    private final Model model;

    /**
     * The File management.
     */
    private FilePath fileManagement;

    /**
     * The Queries dictionary.
     */
    private final Map<String, List<String>> queriesDictionary;

    /**
     * Instantiates a new Jena parser.
     * he data file
     *
     * @param model
     */
    public JenaParser(Model model, Map<String, List<String>> queriesDictionary) {
        this.model = model;
        this.queriesDictionary = new HashMap<>(queriesDictionary);
    }


    public Set<String> doQueryResult(String queryString) {
        Set<String> queriesResults = new HashSet<>();
        Query query = QueryFactory.create(queryString);
        QueryExecution qexec = QueryExecutionFactory.create(query, this.model);
        ResultSet results = qexec.execSelect();
        while (results.hasNext()) {
            QuerySolution soln = results.nextSolution();
            soln.varNames().forEachRemaining(var -> {
                RDFNode node = soln.get(var);
                queriesResults.add(node.toString());
            });
        }
        qexec.close();
        return queriesResults;
    }
}


