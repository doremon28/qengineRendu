package qengineRendu.program.utils;

public class FilePath {
	private final String basUrl;
	private final String workingDir;
	private final String queryFile;
	private final String dataFile;

	public FilePath(String workingDir) {
		this.basUrl = null;
		this.workingDir = workingDir;
		this.queryFile = workingDir + "sample_query.queryset";
		this.dataFile = workingDir + "100K.nt";
	}

	public String getBasUrl() {
		return basUrl;
	}
	public String getWorkingDir() {
		return workingDir;
	}
	public String getQueryFile() {
		return queryFile;
	}
	public String getDataFile() {
		return dataFile;
	}
	
}
