package qengineRendu.program.parser;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import qengineRendu.program.utils.StatisticQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
                int i = 0;
                soln.varNames().forEachRemaining(var -> queryResults.add(soln.get(var).toString()));
            }
        }
        StatisticQuery.incrementQueriesNumberWithoutResponses((int) queryResults.stream().filter(Objects::isNull).count());
        return queryResults.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

}


