package uk.co.novinet.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PersistenceUtils {

    private static final Pattern ISO8601_DATE_PATTERN = Pattern.compile("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}(:\\d{2}\\.\\d{3})?(?<offset>([+-]\\d{2}:\\d{2})|(Z))");

    private static String forumDatabaseTablePrefix;

    private static JdbcTemplate jdbcTemplate;

    @Value("${forumDatabaseTablePrefix}")
    public void setForumDatabaseTablePrefix(String forumDatabaseTablePrefix) {
        PersistenceUtils.forumDatabaseTablePrefix = forumDatabaseTablePrefix;
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        PersistenceUtils.jdbcTemplate = jdbcTemplate;
    }

    public static long unixTime(Instant date) {
        if (date == null) {
            return 0;
        }

        ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant(date, ZoneId.systemDefault());

        String localDateTimeString = zonedDateTime.toOffsetDateTime().toString();

        Matcher matcher = ISO8601_DATE_PATTERN.matcher(localDateTimeString);

        if (matcher.find()) {
            String originalOffset = matcher.group("offset");
            String utcDateString = localDateTimeString.replace(originalOffset, "+00:00");
            return ZonedDateTime.parse(utcDateString).toInstant().toEpochMilli() / 1000;
        }

        throw new RuntimeException("Could not parse date: " + localDateTimeString);
    }

    public static Instant dateFromMyBbRow(ResultSet rs, String columnName) throws SQLException {
        Long dateInSeconds = rs.getLong(columnName);

        if (dateInSeconds != null && dateInSeconds > 0) {
            return new Date(dateInSeconds * 1000L).toInstant();
        }

        return null;
    }

    public static Object like(String argument) {
        return "%" + argument.toLowerCase() + "%";
    }

    public static Where buildWhereClause(List<String> clauses, List<Object> parameters, String operator) {
        String sql = clauses.isEmpty() ? "" : "where ";

        for (int i = 0; i < clauses.size(); i++) {
            sql += clauses.get(i);
            if (i < clauses.size() - 1) {
                sql += " " + operator + " ";
            }
        }

        return new Where(sql, parameters);
    }

    public static String bankTransactionsTableName() {
        return forumDatabaseTablePrefix + "bank_transactions";
    }

    public static String usersTableName() {
        return forumDatabaseTablePrefix + "users";
    }

    public static String claimTableName() {
        return forumDatabaseTablePrefix + "claim_participants";
    }

    public static String userGroupsTableName() {
        return forumDatabaseTablePrefix + "usergroups";
    }

    public static Long findNextAvailableId(String idColumnName, String tableName) {
        Long max = jdbcTemplate.queryForObject("select max(" + idColumnName + ") from " + tableName, Long.class);

        if (max == null) {
            max = (long) 1;
        } else {
            max = max + 1;
        }

        return max;
    }


}
