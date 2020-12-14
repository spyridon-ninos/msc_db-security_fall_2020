package gr.aegean.msc.dbprivacy.core.infra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SqlQueriesConfig {

    private final String countRecords;
    private final String uniqueRecords;
    private final String distinctArmedValues;
    private final String distinctAgeValues;
    private final String distinctGenderValues;
    private final String distinctRaceValues;
    private final String distinctCityPerStateValues;
    private final String distinctCityValues;
    private final String anonymizedRecords;
    private final String recordsPerArmedValues;

    @Autowired
    public SqlQueriesConfig(
            @Value("${sql.queries.countRecords}") String countRecords,
            @Value("${sql.queries.uniqueRecords}") String uniqueRecords,
            @Value("${sql.queries.distinctArmedValues}") String distinctArmedValues,
            @Value("${sql.queries.distinctAgeValues}") String distinctAgeValues,
            @Value("${sql.queries.distinctGenderValues}") String distinctGenderValues,
            @Value("${sql.queries.distinctRaceValues}") String distinctRaceValues,
            @Value("${sql.queries.distinctCityPerStateValues}") String distinctCityPerStateValues,
            @Value("${sql.queries.distinctCityValues}") String distinctCityValues,
            @Value("${sql.queries.anonymizedRecords}") String anonymizedRecords,
            @Value("${sql.queries.recordsPerArmedValues}") String recordsPerArmedValues
    ) {
        this.countRecords = countRecords;
        this.uniqueRecords = uniqueRecords;
        this.distinctArmedValues = distinctArmedValues;
        this.distinctAgeValues = distinctAgeValues;
        this.distinctGenderValues = distinctGenderValues;
        this.distinctRaceValues = distinctRaceValues;
        this.distinctCityPerStateValues = distinctCityPerStateValues;
        this.distinctCityValues = distinctCityValues;
        this.anonymizedRecords = anonymizedRecords;
        this.recordsPerArmedValues = recordsPerArmedValues;
    }

    public String getCountRecords() {
        return countRecords;
    }

    public String getUniqueRecords() {
        return uniqueRecords;
    }

    public String getDistinctArmedValues() {
        return distinctArmedValues;
    }

    public String getDistinctAgeValues() {
        return distinctAgeValues;
    }

    public String getDistinctGenderValues() {
        return distinctGenderValues;
    }

    public String getDistinctRaceValues() {
        return distinctRaceValues;
    }

    public String getDistinctCityPerStateValues() {
        return distinctCityPerStateValues;
    }

    public String getDistinctCityValues() {
        return distinctCityValues;
    }

    public String getAnonymizedRecords() {
        return anonymizedRecords;
    }

    public String getRecordsPerArmedValues() {
        return recordsPerArmedValues;
    }
}

