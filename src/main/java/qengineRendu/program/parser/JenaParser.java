package qengineRendu.program.parser;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.slf4j.Logger;
import qengineRendu.program.utils.StatisticQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Jena parser.
 */
public class JenaParser {
    private final static Logger logger = org.slf4j.LoggerFactory.getLogger(JenaParser.class);
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
        if (queryResults.isEmpty()) {
            logger.info("No results for this query");
        } else {
            logger.info("Number of results for this query : {}", queryResults.size());
            StatisticQuery.incrementQueriesNumberWithoutResponses(1);
        }
        return queryResults.stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

}


