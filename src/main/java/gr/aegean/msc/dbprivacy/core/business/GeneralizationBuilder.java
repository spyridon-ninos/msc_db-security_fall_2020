package gr.aegean.msc.dbprivacy.core.business;

import gr.aegean.msc.dbprivacy.core.infra.DbUtils;
import gr.aegean.msc.dbprivacy.core.model.AnonymizedRecord;
import gr.aegean.msc.dbprivacy.core.model.CityStateTuple;
import java.util.List;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * this class creates the generalization domains for the categorical data
 * we provide
 */
@Component
public final class GeneralizationBuilder {

    private final Logger logger = LoggerFactory.getLogger(GeneralizationBuilder.class);

    private final List<CityStateTuple> cityStateTuples;
    private final GeneralizationStatus generalizationStatus;

    @Autowired
    public GeneralizationBuilder(DbUtils dbUtils) {
        cityStateTuples = dbUtils.getDistinctCityStateValues();
        generalizationStatus = new GeneralizationStatus();
    }

    // 0-10, 11-20, 21-30, 31-40, 41-50, 51-60, 61-70, 71-80, 81-90, 91-100, 100+
    private AnonymizedRecord ageGeneralizeL1(AnonymizedRecord record) {
        var ageNum = Double.parseDouble(record.getAge());
        var result = "100+";

        if (ageNum <= 10.0) {
            result = "0-10";
        } else if (ageNum <= 20.0) {
            result = "11-20";
        } else if (ageNum <= 30.0) {
            result = "21-30";
        } else if (ageNum <= 40.0) {
            result = "31-40";
        } else if (ageNum <= 50.0) {
            result = "41-50";
        } else if (ageNum <= 60.0) {
            result = "51-60";
        } else if (ageNum <= 70.0) {
            result = "61-70";
        } else if (ageNum <= 80.0) {
            result = "71-80";
        } else if (ageNum <= 90.0) {
            result = "81-90";
        } else if (ageNum <= 100.0) {
            result = "91-100";
        }
        
        logger.debug("Age: {} -> L1 category: {}", ageNum, result);

        record.setAge(result);
        return record;
    }

    // 0-30, 31-60, 61-90, 90+
    private AnonymizedRecord ageGeneralizeL2(AnonymizedRecord record) {
        var ageNum = Double.parseDouble(record.getAge().split("-")[0]);
        var result = "90+";
        
        if (ageNum <= 30.0) {
            result = "0-30";
        } else if (ageNum <= 60.0) {
            result = "31-60";
        } else if (ageNum <= 90.0) {
            result = "61-90";
        }
        
        logger.debug("Age: {} -> L2 category: {}", ageNum, result);

        record.setAge(result);
        return record;
    }

    private AnonymizedRecord ageGeneralizeL3(AnonymizedRecord record) {
        logger.debug("Suppressing age {}", record.getAge());
        record.setAge("*");
        return record;
    }

    private AnonymizedRecord genderGeneralizeL1(AnonymizedRecord record) {
        logger.debug("Suppressing gender {}", record.getGender());
        record.setGender("*");
        return record;
    }

    private AnonymizedRecord cityGeneralizeL1(AnonymizedRecord record) {
        var cityStateTuple = cityStateTuples.stream()
                                            .filter(tuple -> record.getCity().equals(tuple.getCity()))
                                            .findAny();

        cityStateTuple.ifPresent(tuple -> {
            logger.debug("City {} -> state {}", record.getCity(), tuple.getState());
            record.setCity(tuple.getState());
        });

        return record;
    }

    private AnonymizedRecord cityGeneralizeL2(AnonymizedRecord record) {
        logger.debug("State {} -> USA", record.getCity());
        record.setCity("USA");
        return record;
    }

    private AnonymizedRecord cityGeneralizeL3(AnonymizedRecord record) {
        logger.debug("Suppressing city: {}", record.getCity());
        record.setCity("*");
        return record;
    }

    private AnonymizedRecord raceGeneralizeL1(AnonymizedRecord record) {
        logger.debug("Race: {} -> North-American", record.getRace());
        record.setRace("North-American");
        return record;
    }

    private AnonymizedRecord raceGeneralizeL2(AnonymizedRecord record) {
        logger.debug("Suppressing race: {}", record.getRace());
        record.setRace("*");
        return record;
    }

    public GeneralizationStatus increaseGeneralizationDomain(
            List<AnonymizedRecord> anonymizedRecords
    ) {

        if (generalizationStatus.getAgeCurrentDomain() == 0) {
            generalizationStatus.increaseAgeDomain();
            generalizeRecords(anonymizedRecords, this::ageGeneralizeL1);
        } else if (generalizationStatus.getCityCurrentDomain() == 0) {
            generalizationStatus.increaseCityDomain();
            generalizeRecords(anonymizedRecords, this::cityGeneralizeL1);
        } else if (generalizationStatus.getGenderCurrentDomain() == 0) {
            generalizationStatus.increaseGenderDomain();
            generalizeRecords(anonymizedRecords, this::genderGeneralizeL1);
        } else if (generalizationStatus.getRaceCurrentDomain() == 0) {
            generalizationStatus.increaseRaceDomain();
            generalizeRecords(anonymizedRecords, this::raceGeneralizeL1);
        } else if (generalizationStatus.getAgeCurrentDomain() == 1) {
            generalizationStatus.increaseAgeDomain();
            generalizeRecords(anonymizedRecords, this::ageGeneralizeL2);
        } else if (generalizationStatus.getCityCurrentDomain() == 1) {
            generalizationStatus.increaseCityDomain();
            generalizeRecords(anonymizedRecords, this::cityGeneralizeL2);
        } else if (generalizationStatus.getRaceCurrentDomain() == 1) {
            generalizationStatus.increaseRaceDomain();
            generalizeRecords(anonymizedRecords, this::raceGeneralizeL2);
        } else if (generalizationStatus.getAgeCurrentDomain() == 2) {
            generalizationStatus.increaseAgeDomain();
            generalizeRecords(anonymizedRecords, this::ageGeneralizeL3);
        } else if (generalizationStatus.getCityCurrentDomain() == 2) {
            generalizationStatus.increaseCityDomain();
            generalizeRecords(anonymizedRecords, this::cityGeneralizeL3);
        }

        return generalizationStatus;
    }
    
    private void generalizeRecords(List<AnonymizedRecord> records, Consumer<AnonymizedRecord> action) {
        for(AnonymizedRecord record: records) {
            action.accept(record);
        }
    }

    public boolean isNotFullySuppressed() {
        return generalizationStatus.isNotFullySuppressed();
    }
    
    public double getInformationLoss() {
        return generalizationStatus.getInformationLoss();
    }

    public String getCurrentGeneralizationLevels() {
        return generalizationStatus.getCurrentGeneralizationLevels();
    }

    public String describeGeneralizationDomains() {
        return generalizationStatus.describeGeneralizationDomains();
    }

    public void reset() {
        generalizationStatus.reset();
    }

    public static class GeneralizationStatus {

        private final Logger logger = LoggerFactory.getLogger(GeneralizationStatus.class);

        private final List<String> ageDomains;
        private int ageCurrentDomain;
        private final List<String> genderDomains;
        private int genderCurrentDomain;
        private final List<String> raceDomains;
        private int raceCurrentDomain;
        private final List<String> cityDomains;
        private int cityCurrentDomain;

        public int getAgeCurrentDomain() {
            return ageCurrentDomain;
        }

        public void increaseAgeDomain() {
            if (ageCurrentDomain < 3) {
                ageCurrentDomain++;
            }
        }

        public int getGenderCurrentDomain() {
            return genderCurrentDomain;
        }

        public void increaseGenderDomain() {
            if (genderCurrentDomain < 1) {
                genderCurrentDomain++;
            }
        }

        public int getRaceCurrentDomain() {
            return raceCurrentDomain;
        }

        public void increaseRaceDomain() {
            if (raceCurrentDomain < 2) {
                raceCurrentDomain++;
            }
        }

        public int getCityCurrentDomain() {
            return cityCurrentDomain;
        }

        public void increaseCityDomain() {
            if (cityCurrentDomain < 3) {
                cityCurrentDomain++;
            }
        }

        public GeneralizationStatus() {
            ageDomains = List.of("L0", "L1", "L2", "*"); // distinct, every 10 years, every 30 years, any
            ageCurrentDomain = 0;

            genderDomains = List.of("L0", "*"); // distinct, any
            genderCurrentDomain = 0;

            raceDomains = List.of("L0", "L1", "*"); // distinct, North-American, any
            raceCurrentDomain = 0;

            cityDomains = List.of("L0", "L1", "L2", "*"); // city, state, USA, any
            cityCurrentDomain = 0;
        }

        public void reset() {
            ageCurrentDomain = 0;
            genderCurrentDomain = 0;
            raceCurrentDomain = 0;
            cityCurrentDomain = 0;
        }

        public boolean isNotFullySuppressed() {
            return ageCurrentDomain < 3 || genderCurrentDomain < 1 || raceCurrentDomain < 2 || cityCurrentDomain < 3;
        }

        public double getInformationLoss() {
            int currentDomains = ageCurrentDomain + genderCurrentDomain + raceCurrentDomain + cityCurrentDomain;
            int domainDepths = (ageDomains.size()-1) + (genderDomains.size()-1) + (raceDomains.size()-1) + (cityDomains.size()-1);
            logger.debug("age: {} gender: {} race: {} city: {}, totalDomains: {}, totalDepths: {}, infoloss: {}",
                    ageCurrentDomain,
                    genderCurrentDomain,
                    raceCurrentDomain,
                    cityCurrentDomain,
                    currentDomains,
                    domainDepths,
                    (double) currentDomains/(double)domainDepths
            );
            return (double) currentDomains/(double)domainDepths;
        }

        public String getCurrentGeneralizationLevels() {
            return "age -> " + ageDomains.get(ageCurrentDomain) + "\n" +
                    "city -> " + cityDomains.get(cityCurrentDomain) + "\n" +
                    "race -> " + raceDomains.get(raceCurrentDomain) + "\n" +
                    "gender -> " + genderDomains.get(genderCurrentDomain) + "\n";
        }

        public String describeGeneralizationDomains() {
            return "age:\n" +
                    "L0: 0-10, 11-20, 21-30, 31-40, 41-50, 51-60, 61-70, 71-80, 81-90, 91-100, 100+\n" +
                    "L1: 0-30, 31-60, 61-90, 90+\n" +
                    "L2: *\n" +
                    "\ncity: \n" +
                    "L0: (all cities in the dataset)\n" +
                    "L1: (all cities' respective states in the dataset)\n" +
                    "L2: USA\n" +
                    "L3: *\n" +
                    "\nrace:\n" +
                    "L0: Hispanic, Black, White, Asian, Other\n" +
                    "L1: North-American\n" +
                    "L2: *\n" +
                    "\ngender:\n" +
                    "L0: M, F\n" +
                    "L1 *\n";
        }
    }
}
