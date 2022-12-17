package qengineRendu.program.utils;

import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

public class StatisticQuery {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(StatisticQuery.class);
    private static final Map<String, List<StatisticQuery>> statisticQueriesInFiles = new HashMap<>();
    private static final List<StatisticQuery> statisticQueriesInFile = new ArrayList<>();

    private final String fileName;
    private final String queryName;

    private final int queryNumber;
    private final double timeReadingQuery;

    private static int queriesNumberWithoutResponses = 0;

    public List<String[]> getStatisticsQueriesCsv() {
        List<String[]> res = new ArrayList<>();
        res.add(new String[]{"File Name", "Query name", "Query number", "Time execution",});
        statisticQueriesInFile
                .stream()
                .map(s -> new String[]{
                        String.valueOf(s.getFileName()),
                        String.valueOf(s.getQueryName()),
                        String.valueOf(s.getQueryNumber()),
                        String.valueOf(s.getTimeReadingQuery()),})
                .forEach(res::add);
        return res;
    }

    public static void refreshStatisticQueriesInFile() {
        statisticQueriesInFile.clear();
    }
    public static List<StatisticQuery> getAllValuesInFiles() {
        return statisticQueriesInFiles
                .values()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
    public int getQueryNumber() {
        return queryNumber;
    }

    public String getFileName() {
        return fileName;
    }

    public String getQueryName() {
        return queryName;
    }

    public double getTimeReadingQuery() {
        return timeReadingQuery;
    }

    public StatisticQuery(String queryFile, String queryName, long timeExecution, int queryNumber) {
        this.fileName = queryFile;
        this.queryName = queryName;
        this.queryNumber = queryNumber;
        this.timeReadingQuery = (timeExecution / 1_000_000.0);
        statisticQueriesInFile.add(this);
    }
    public static double getTimeReadingQueryInFileByName(String fileName) {
        return statisticQueriesInFiles
                .get(fileName)
                .stream()
                .mapToDouble(StatisticQuery::getTimeReadingQuery)
                .sum();
    }
    public static List<StatisticQuery> getStatisticQueriesInFile() {
        return statisticQueriesInFile;
    }

    public static Map<String, List<StatisticQuery>> getStatisticQueriesInFiles() {
        return statisticQueriesInFiles;
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


    public static double getTotalTimeExecutionStaticInFile() {
        double somme = 0;
        for (StatisticQuery s : statisticQueriesInFile) {
            somme += s.getTimeReadingQuery();
        }
        return somme;
    }

    public static double getTotalTimeExecutionInFiles() {
        double somme = 0;
        for (StatisticQuery s : getAllValuesInFiles()) {
            somme += s.getTimeReadingQuery();
        }
        return somme;
    }

    public static int getTotalQueryNumberInFiles() {
        return getAllValuesInFiles().size();
    }

    @Override
    public String toString() {
        return ("StatisticQuery{" + "fileName='" + fileName + '\'' + ", queryName='" + queryName + '\'' + ", queryNumber=" + queryNumber + ", timeReadingQuery=" + timeReadingQuery + '}');
    }
}
