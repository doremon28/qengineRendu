package qengineRendu.program.utils;

public class FilePath {
	private final String basUrl;
	private final String queryFile;
	private final String dataFile;
	private final String outputFolder;

	public FilePath(String queryFile, String dataFile, String outputFolder) {
		this.basUrl = null;
		this.queryFile = queryFile;
		this.dataFile = dataFile;
		this.outputFolder = outputFolder;
	}

	public String getBasUrl() {
		return basUrl;
	}
	public String getQueryFile() {
		return queryFile;
	}
	public String getDataFile() {
		return dataFile;
	}

	public String getOutputFolder() {
		return outputFolder;
	}
}
