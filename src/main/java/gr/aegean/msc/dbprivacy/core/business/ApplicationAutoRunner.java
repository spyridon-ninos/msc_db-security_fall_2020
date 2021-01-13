package gr.aegean.msc.dbprivacy.core.business;

import gr.aegean.msc.dbprivacy.core.infra.DbUtils;
import gr.aegean.msc.dbprivacy.core.model.AnonymizedRecord;
import gr.aegean.msc.dbprivacy.core.model.CityStateTuple;
import java.text.DecimalFormat;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

@Component
public class ApplicationAutoRunner {

    private final Logger logger = LoggerFactory.getLogger(ApplicationAutoRunner.class);

    private final DbUtils dbUtils;
    private final GeneralizationTaxonomyBuilder generalizationTaxonomyBuilder;

    @Autowired
    public ApplicationAutoRunner(
            DbUtils dbUtils,
            GeneralizationTaxonomyBuilder generalizationTaxonomyBuilder
    ) {
        this.dbUtils = dbUtils;
        this.generalizationTaxonomyBuilder = generalizationTaxonomyBuilder;
    }

    @EventListener
    public void run(ApplicationReadyEvent event) {

        logger.warn("\n*** MSc Information and Communication Systems Security, Aegean Univ., Fall 2020, DB Security ***");
        logger.warn("\nMembers of the team: \n- Spyridon Ninos (3232020022)\n- Christos Skatharoudis (3232020025)\n- Margarita Bogdanou (3232020021)");
        logger.warn("\nUsing the dataset from: https://www.kaggle.com/ahsen1330/us-police-shootings");

        String generalizationDomainsDescription = generalizationTaxonomyBuilder.describeGeneralizationDomains();
        logger.warn("\nDescription of the generalization domains used in this program:\n{}", generalizationDomainsDescription);

        var totalRecords = dbUtils.getNumOfRecords();
        logger.warn("Loaded {} anonymized records (Identifying: name (excluded), QI: race, gender, age, SA: armed, Insensitive: city)\n", totalRecords);

        var uniqueRecords = dbUtils.getUniqueRecords();

        if (uniqueRecords > 0) {
            var percentage = ((double) uniqueRecords / totalRecords) * 100;
            var formattedPercentage = new DecimalFormat("##.##").format(percentage);
            logger.warn("Found {} unique records - {}% of the total set\n", uniqueRecords, formattedPercentage);
        } else {
            logger.warn("Found no unique records\n");
        }

        if (logger.isDebugEnabled()) {

            var recordsPerArmedValues = dbUtils.getRecordsPerArmedValues();
            logger.warn("Records per armed: ");
            recordsPerArmedValues.keySet().forEach(key -> logger.warn("{}: {}", key, recordsPerArmedValues.get(key)));
            logger.warn("\n");

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

        var anonymizedRecords = dbUtils.getAnonymizedRecords();

        var kAnonymizer = new KAnonymity(anonymizedRecords, generalizationTaxonomyBuilder);

        kAnonymizer.setRoundToModifySet(3);
        List<AnonymizedRecord> modifiedSet = null;
        for (int k=1; k<10; k++) {
            kAnonymizer.check(k);
            if (k == 3) {
                modifiedSet = kAnonymizer.getModifiedAnonymizedRecords();
            }
        }

        logger.warn("\n\n\n\n\n===================================================================\n\n\n\n");

        var kAnonymizer2 = new KAnonymity(modifiedSet, generalizationTaxonomyBuilder, new LDiversity());
        for (int k=1; k<10; k++) {
            kAnonymizer2.check(k);
        }
    }
}
