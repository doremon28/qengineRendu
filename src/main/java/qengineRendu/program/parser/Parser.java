package qengineRendu.program.parser;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

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
 * The type Parser.
 */
public class Parser {
	/**
	 * The constant logger.
	 */
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(Parser.class);
	/**
	 * The File management.
	 */
	private final FilePath fileManagement;

	/**
	 * Instantiates a new Parser.
	 *
	 * @param fileManagement the file management
	 */
	public Parser(FilePath fileManagement) {
		this.fileManagement = fileManagement;
	}

	/**
	 * Parse data.
	 *
	 * @throws RDFParseException   the rdf parse exception
	 * @throws RDFHandlerException the rdf handler exception
	 * @throws IOException         the io exception
	 */
	private void parseData() throws RDFParseException, RDFHandlerException, IOException {
		if (this.fileManagement != null &&
				!this.fileManagement.getDataFile().isEmpty()) {
			try (Reader dataReader = new FileReader(fileManagement.getDataFile())) {
				// On va parser des donnees au format ntriples
				RDFParser rdfParser = Rio.createParser(RDFFormat.NTRIPLES);
				
				// On utilise notre implementation de handler
				rdfParser.setRDFHandler(new MainRDFHandler());

				// Parsing et traitement de chaque triple par le handler
				long startTimeReadingData = System.nanoTime();
				rdfParser.parse(dataReader, fileManagement.getBasUrl());
				long endTimeReadingData = System.nanoTime();
				StatisticData.timeReadingData = (endTimeReadingData - startTimeReadingData) / 1_000_000.0;
				logger.info("Reading data time: {} ms", StatisticData.timeReadingData);
			}
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
	public void parse() throws RDFParseException, RDFHandlerException, IOException {
		parseData();
	}
}
