package qengineRendu.program.parser;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFHandlerException;
import org.eclipse.rdf4j.rio.RDFParseException;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import qengineRendu.program.operations.MainRDFHandler;
import qengineRendu.program.utils.FilePath;
import qengineRendu.program.utils.StatisticData;

/**
 * The type DataParser.
 */
public class DataParser {
    /**
     * The constant logger.
     */
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(DataParser.class);
    /**
     * The File management.
     */
    private final FilePath fileManagement;
    private static Model model;

    /**
     * Instantiates a new DataParser.
     *
     * @param fileManagement the file management
     */
    public DataParser(FilePath fileManagement) {
        this.fileManagement = fileManagement;
    }

    /**
     * Parse data.
     *
     * @throws RDFParseException   the rdf parse exception
     * @throws RDFHandlerException the rdf handler exception
     * @throws IOException         the io exception
     */
    private void parseDataRdf() throws RDFParseException, RDFHandlerException, IOException {
        if (this.fileManagement != null &&
                !this.fileManagement.getDataFile().isEmpty()) {
            try (Reader dataReader = new FileReader(fileManagement.getDataFile())) {
                RDFParser rdfParser = Rio.createParser(RDFFormat.NTRIPLES);
                rdfParser.setRDFHandler(new MainRDFHandler());
                Long start = System.nanoTime();
                rdfParser.parse(dataReader, fileManagement.getBasUrl());
                Long end = System.nanoTime();
                StatisticData.timeReadingData = (end - start) / 1_000_000.0;
                logger.info("Reading data time: {} ms", StatisticData.timeReadingData);

            }


        } else {
            logger.error("Error: fileManagement is null or dataFile is empty");
        }

    }

    private void parseDataJena() throws RDFParseException, RDFHandlerException {
        if (this.fileManagement != null &&
                !this.fileManagement.getDataFile().isEmpty()) {
            InputStream in = RDFDataMgr.open(fileManagement.getDataFile());
            model = ModelFactory.createDefaultModel();
            Long start = System.currentTimeMillis();
            model.read(in, null, "N-TRIPLES");
            Long end = System.currentTimeMillis();
            Long time = end - start == 0 ? 1 : end - start;
            StatisticData.timeReadingData = time;
            logger.info("Reading data time: {} ms", StatisticData.timeReadingData);

        } else {
            logger.error("Error: fileManagement is null or dataFile is empty");
        }

    }


    /**
     * Parse.
     *
     * @throws RDFParseException   the rdf parse exception
     * @throws RDFHandlerException the rdf handler exception
     * @throws IOException         the io exception
     */
    public void parse(boolean isJenna) throws RDFParseException, RDFHandlerException, IOException {
        if (isJenna) {
            logger.info("Parsing data with Jena");
            parseDataJena();
            parseDataRdf();
        } else {
            logger.info("Parsing data with rdf parser");
            parseDataRdf();
        }
    }

    public static Model getModel() {
        return model;
    }
}
