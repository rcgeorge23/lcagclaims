package uk.co.novinet.service;

import java.util.List;

public class Where {
    private String sql;
    private List<Object> arguments;

    public Where(String sql, List<Object> arguments) {
        this.sql = sql;
        this.arguments = arguments;
    }

    public String getSql() {
        return sql;
    }

    public List<Object> getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return "Where{" +
                "sql='" + sql + '\'' +
                ", arguments=" + arguments +
                '}';
    }
}
