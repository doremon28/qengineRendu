package qengineRendu.program.utils;

import java.util.ArrayList;
import java.util.List;

public class StatisticQuery {
    private static final List<StatisticQuery> statisticQueries = new ArrayList<>();
    private final String queryName;
    private final int queryNumber;
    private final double timeExecution;

    public String getQueryName() {
        return queryName;
    }

    public int getQueryNumber() {
        return queryNumber;
    }

    public double getTimeExecution() {
        return timeExecution;
    }
    public StatisticQuery(String queryName, int queryNumber, long timeExecution) {
        this.queryName = queryName;
        this.queryNumber = queryNumber;
        this.timeExecution = (timeExecution / 1_000_000.0);
        statisticQueries.add(this);
    }

    public static List<StatisticQuery> getStatisticQueries() {
        return statisticQueries;
    }

    public static int getTotalNumberOfQueries() {
        return statisticQueries.size();
    }

    public static Long getTotalTimeExecution() {
        return statisticQueries.stream().mapToLong(s -> (long) s.getTimeExecution()).sum();
    }

    @Override
    public String toString() {
        return "StatisticQuery{" +
                "queryName='" + queryName + '\'' +
                ", queryNumber=" + queryNumber +
                ", timeExecution=" + timeExecution + " ms" +
                '}';
    }
}
