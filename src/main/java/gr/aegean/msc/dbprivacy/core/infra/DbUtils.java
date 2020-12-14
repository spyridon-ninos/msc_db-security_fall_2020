package gr.aegean.msc.dbprivacy.core.infra;

import gr.aegean.msc.dbprivacy.core.model.AnonymizedRecord;
import gr.aegean.msc.dbprivacy.core.model.CityStateTuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

@Component
public final class DbUtils {

    private final Logger logger = LoggerFactory.getLogger(DbUtils.class);

    private final SqlQueriesConfig sqlQueriesConfig;
    private final JdbcTemplate jdbcTemplate;
    private final CityStateTupleMapper cityStateTupleMapper;
    private final AnonymizedRecordMapper anonymizedRecordMapper;
    private final RecordsPerArmedMapper recordsPerArmedMapper;

    @Autowired
    public DbUtils(
        SqlQueriesConfig sqlQueriesConfig,
        JdbcTemplate jdbcTemplate,
        CityStateTupleMapper cityStateTupleMapper,
        AnonymizedRecordMapper anonymizedRecordMapper,
        RecordsPerArmedMapper recordsPerArmedMapper
    ) {
        this.sqlQueriesConfig = sqlQueriesConfig;
        this.jdbcTemplate = jdbcTemplate;
        this.cityStateTupleMapper = cityStateTupleMapper;
        this.anonymizedRecordMapper = anonymizedRecordMapper;
        this.recordsPerArmedMapper = recordsPerArmedMapper;
    }

    public int getNumOfRecords() {
        var countRecords = jdbcTemplate.queryForObject(sqlQueriesConfig.getCountRecords(), Integer.class);
        return Optional.ofNullable(countRecords)
                       .orElse(0);
    }

    public int getUniqueRecords() {
        var uniqueRecords = jdbcTemplate.queryForObject(sqlQueriesConfig.getUniqueRecords(), Integer.class);
        return Optional.ofNullable(uniqueRecords).orElse(0);
    }

    public List<String> getDistinctArmedValues() {
        return jdbcTemplate.queryForList(sqlQueriesConfig.getDistinctArmedValues(), String.class);
    }

    public List<String> getDistinctAgeValues() {
        return jdbcTemplate.queryForList(sqlQueriesConfig.getDistinctAgeValues(), String.class);
    }

    public List<String> getDistinctGenderValues() {
        return jdbcTemplate.queryForList(sqlQueriesConfig.getDistinctGenderValues(), String.class);
    }

    public List<String> getDistinctRaceValues() {
        return jdbcTemplate.queryForList(sqlQueriesConfig.getDistinctRaceValues(), String.class);
    }

    public List<String> getDistinctCityValues() {
        return jdbcTemplate.queryForList(sqlQueriesConfig.getDistinctCityValues(), String.class);
    }

    public List<CityStateTuple> getDistinctCityStateValues() {
        return jdbcTemplate.query(sqlQueriesConfig.getDistinctCityPerStateValues(), cityStateTupleMapper);
    }

    public List<AnonymizedRecord> getAnonymizedRecords() {
        return jdbcTemplate.query(sqlQueriesConfig.getAnonymizedRecords(), anonymizedRecordMapper);
    }

    public Map<String, Integer> getRecordsPerArmedValues() {
        return jdbcTemplate.query(sqlQueriesConfig.getRecordsPerArmedValues(), recordsPerArmedMapper)
                           .stream()
                           .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

    }
}
