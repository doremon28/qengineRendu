package qengineRendu.program.utils;

import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Statistic query.
 */
public class StatisticQuery {
    /**
     * The constant logger.
     */
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(StatisticQuery.class);
    /**
     * The constant statisticQueriesInFiles.
     */
    private static final Map<String, List<StatisticQuery>> statisticQueriesInFiles = new HashMap<>();
    /**
     * The constant statisticQueriesInFile.
     */
    private static final List<StatisticQuery> statisticQueriesInFile = new ArrayList<>();

    private static int queriesNumberWithoutResponses = 0;
    /**
     * The File name.
     */
    private final String fileName;
    /**
     * The Query name.
     */
    private final String queryName;

    /**
     * The Query number.
     */
    private final int queryNumber;
    /**
     * The Time reading query.
     */
    private final double timeReadingQuery;

    /**
     * Gets statistics queries csv.
     *
     * @return the statistics queries csv
     */
    public static List<String[]> getStatisticsQueriesCsv() {
        List<String[]> res = new ArrayList<>();
        res.add(new String[]{"File Name", "Query name", "Query number", "Time execution",});
        getAllValuesInFiles()
                .stream()
                .map(s -> new String[]{
                        String.valueOf(s.getFileName()),
                        String.valueOf(s.getQueryName()),
                        String.valueOf(s.getQueryNumber()),
                        String.valueOf(s.getTimeReadingQuery()),})
                .forEach(res::add);
        return res;
    }

    public static int getQueriesNumberWithoutResponses() {
        return queriesNumberWithoutResponses;
    }

    public static void incrementQueriesNumberWithoutResponses(int increment) {
        queriesNumberWithoutResponses += increment;
    }

    public static void incrementQueriesNumberWithoutResponsesByOne() {
        queriesNumberWithoutResponses++;
    }

    /**
     * Refresh statistic queries in file.
     */
    public static void refreshStatisticQueriesInFile() {
        statisticQueriesInFile.clear();
    }

    /**
     * Gets all values in files.
     *
     * @return the all values in files
     */
    public static List<StatisticQuery> getAllValuesInFiles() {
        return statisticQueriesInFiles
                .values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    /**
     * Gets query number.
     *
     * @return the query number
     */
    public int getQueryNumber() {
        return queryNumber;
    }

    /**
     * Gets file name.
     *
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Gets query name.
     *
     * @return the query name
     */
    public String getQueryName() {
        return queryName;
    }

    /**
     * Gets time reading query.
     *
     * @return the time reading query
     */
    public double getTimeReadingQuery() {
        return timeReadingQuery;
    }

    /**
     * Instantiates a new Statistic query.
     *
     * @param queryFile     the query file
     * @param queryName     the query name
     * @param timeExecution the time execution
     * @param queryNumber   the query number
     */
    public StatisticQuery(String queryFile, String queryName, long timeExecution, int queryNumber) {
        this.fileName = queryFile;
        this.queryName = queryName;
        this.queryNumber = queryNumber;
        this.timeReadingQuery = (timeExecution / 1_000_000.0);
        statisticQueriesInFile.add(this);
    }

    /**
     * Gets time reading query in file by name.
     *
     * @param fileName the file name
     * @return the time reading query in file by name
     */
    public static double getTimeReadingQueryInFileByName(String fileName) {
        return statisticQueriesInFiles
                .get(fileName)
                .stream()
                .mapToDouble(StatisticQuery::getTimeReadingQuery)
                .sum();
    }

    /**
     * Gets statistic queries in file.
     *
     * @return the statistic queries in file
     */
    public static List<StatisticQuery> getStatisticQueriesInFile() {
        return statisticQueriesInFile;
    }

    /**
     * Gets statistic queries in files.
     *
     * @return the statistic queries in files
     */
    public static Map<String, List<StatisticQuery>> getStatisticQueriesInFiles() {
        return statisticQueriesInFiles;
    }


    /**
     * Gets total time execution static in file.
     *
     * @return the total time execution static in file
     */
    public static double getTotalTimeExecutionStaticInFile() {
        double somme = 0;
        for (StatisticQuery s : statisticQueriesInFile) {
            somme += s.getTimeReadingQuery();
        }
        return somme;
    }

    /**
     * Gets total time execution in files.
     *
     * @return the total time execution in files
     */
    public static double getTotalTimeExecutionInFiles() {
        double somme = 0;
        for (StatisticQuery s : getAllValuesInFiles()) {
            somme += s.getTimeReadingQuery();
        }
        return somme;
    }

    /**
     * Gets total query number in files.
     *
     * @return the total query number in files
     */
    public static int getTotalQueryNumberInFiles() {
        return getAllValuesInFiles().size();
    }

    @Override
    public String toString() {
        return ("StatisticQuery{" + "fileName='" + fileName + '\'' + ", queryName='" + queryName + '\'' + ", queryNumber=" + queryNumber + ", timeReadingQuery=" + timeReadingQuery + '}');
    }
}
