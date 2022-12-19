package qengineRendu.program.parser;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;

import java.util.ArrayList;
import java.util.List;

/**
 * Jena parser.
 */
public class JenaParser {
    /**
     * The constant model.
     */
    private static final Model model = ModelFactory.createDefaultModel();
    /**
     * The Data file.
     */
    private String dataFile;

    /**
     * Instantiates a new Jena parser.
     *
     * @param dataFile the data file
     */
    public JenaParser(String dataFile) {
        this.dataFile = dataFile;
        RDFDataMgr.read(model, dataFile);
    }

    /**
     * Jena parser queries factory.
     *
     * @param queryString the query string
     * @return the list
     */
    public List<String> jenaParser(String queryString) {
        List<String> queryResults = new ArrayList<>();
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            while (results.hasNext()) {
                QuerySolution soln = results.nextSolution();
                soln.varNames().forEachRemaining(var -> queryResults.add(soln.get(var).toString()));
            }
        }
        return queryResults;
    }

}


