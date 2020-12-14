package gr.aegean.msc.dbprivacy.core.business;

import gr.aegean.msc.dbprivacy.core.infra.DbUtils;
import gr.aegean.msc.dbprivacy.core.model.CityStateTuple;
import org.deidentifier.arx.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

@Component
public class ApplicationAutoRunner {

    private final Logger logger = LoggerFactory.getLogger(ApplicationAutoRunner.class);

    private final DbUtils dbUtils;
    private final KAnonymity kAnonymity;
    private final HierarchyBuilder hierarchyBuilder;

    @Autowired
    public ApplicationAutoRunner(
            DbUtils dbUtils,
            KAnonymity kAnonymity,
            HierarchyBuilder hierarchyBuilder
    ) {
        this.dbUtils = dbUtils;
        this.kAnonymity = kAnonymity;
        this.hierarchyBuilder = hierarchyBuilder;
    }

    @EventListener
    public void run(ApplicationReadyEvent event) {

        logger.warn("\n*** MSc Information and Communication Systems Security, Aegean Univ., Fall 2020, DB Security ***");
        logger.warn("\nMembers of the team: \n- Spyridon Ninos\n- Christos Skatharoudis\n- Margarita Bogdanou");
        logger.warn("\nUsing the dataset from: https://www.kaggle.com/ahsen1330/us-police-shootings");

        var totalRecords = dbUtils.getNumOfRecords();
        logger.warn("Loaded {} anonymized records (name excluded, QIs: race, gender, age, SA: armed, Insensitive: city)\n", totalRecords);

        var uniqueRecords = dbUtils.getUniqueRecords();

        if (uniqueRecords > 0) {
            var percentage = ((double) uniqueRecords / totalRecords) * 100;
            logger.warn("Found {} unique records - {}% of the total set\n", uniqueRecords, new DecimalFormat("##.##").format(percentage));
        } else {
            logger.warn("Found no unique records\n");
        }

//        if (logger.isDebugEnabled()) {

            var recordsPerArmedValues = dbUtils.getRecordsPerArmedValues();
            logger.warn("Records per armed: ");
            recordsPerArmedValues.keySet().stream().forEach(key -> logger.warn("{}: {}", key, recordsPerArmedValues.get(key)));
            logger.warn("\n");

        if (logger.isDebugEnabled()) {
            var ageValues = String.join(", ", dbUtils.getDistinctAgeValues());
            logger.warn("Age category: {}\n", ageValues);

            var genderValues = String.join(", ", dbUtils.getDistinctGenderValues());
            logger.warn("Genders: {}\n", genderValues);

            var raceValues = String.join(", ", dbUtils.getDistinctRaceValues());
            logger.warn("Races: {}\n", raceValues);

            var cityValues = String.join(", ", dbUtils.getDistinctCityValues());
            logger.warn("Cities: {}\n", cityValues);

            var cityStateValuesMap = dbUtils.getDistinctCityStateValues()
                                            .stream()
                                            .collect(groupingBy(CityStateTuple::getState, mapping(CityStateTuple::getCity, toList())));
            logger.warn("States -> cities: {}", cityStateValuesMap);
        }

        Data.DefaultData data = hierarchyBuilder.loadData(dbUtils.getAnonymizedRecords(), dbUtils.getDistinctCityStateValues());
        kAnonymity.run(data);
    }
}
