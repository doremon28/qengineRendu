package qengineRendu.program.parser;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;

import java.util.ArrayList;
import java.util.List;

public class JenaParser {
    private static final Model model = ModelFactory.createDefaultModel();
    private String dataFile;

    public JenaParser(String dataFile) {
        this.dataFile = dataFile;
        RDFDataMgr.read(model, dataFile);
    }

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


