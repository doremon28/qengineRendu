package qengineRendu.program.parser;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;

import java.util.ArrayList;
import java.util.List;

public class JenaParser {
    private static Model model = ModelFactory.createDefaultModel();

    public List<String> jenaParser(String dataFile, String queryString) throws Exception {
        List<String> queryResults = new ArrayList<>();
        RDFDataMgr.read(model, dataFile);
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();
                results.getResultVars().forEach(var -> queryResults.add(soln.get(var).toString()));
            }
        }
        return queryResults;
    }

    public static void main(String[] args) throws Exception {

        RDFDataMgr.read(model, "data/100K.nt");

//        String queryString = "SELECT ?v0 WHERE {\r\n"
//                + " ?v0 <http://schema.org/eligibleRegion> <http://db.uwaterloo.ca/~galuc/wsdbm/Country137> . } \r\n"
//                + "";
        String queryString = "SELECT ?v0 WHERE {\t?v0 <http://schema.org/eligibleRegion> <http://db.uwaterloo.ca/~galuc/wsdbm/Country137> . } ";
        Query query = QueryFactory.create(queryString);
        try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
            ResultSet results = qexec.execSelect();
            for (; results.hasNext();) {
                QuerySolution soln = results.nextSolution();
//                System.out.println(soln);
                for(String var : results.getResultVars()) {
                    System.out.println(var + " = " + soln.get(var));
                }


            }
        }
    }

}


