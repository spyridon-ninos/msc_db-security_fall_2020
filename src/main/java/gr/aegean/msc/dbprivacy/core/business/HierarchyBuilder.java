package gr.aegean.msc.dbprivacy.core.business;

import gr.aegean.msc.dbprivacy.core.infra.DbUtils;
import gr.aegean.msc.dbprivacy.core.model.AnonymizedRecord;
import gr.aegean.msc.dbprivacy.core.model.CityStateTuple;
import org.deidentifier.arx.AttributeType;
import org.deidentifier.arx.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toMap;

@Component
public final class HierarchyBuilder {

    private final Logger logger = LoggerFactory.getLogger(HierarchyBuilder.class);

    private final DbUtils dbUtils;

    @Autowired
    public HierarchyBuilder(
            DbUtils dbUtils
    ) {
        this.dbUtils = dbUtils;
    }

    public Data.DefaultData loadData(List<AnonymizedRecord> records, List<CityStateTuple> tuples) {
        var defaultData = Data.create();
        defaultData.add("armed", "age", "gender", "race", "city");
        records.forEach(record -> addRecordToData(record, defaultData));

        defaultData.getDefinition().setAttributeType("armed", AttributeType.SENSITIVE_ATTRIBUTE);

        defaultData.getDefinition().setAttributeType("age", AttributeType.QUASI_IDENTIFYING_ATTRIBUTE);
        defaultData.getDefinition().setAttributeType("age", createAgeHierarchy(records));

        defaultData.getDefinition().setAttributeType("gender", AttributeType.QUASI_IDENTIFYING_ATTRIBUTE);
        defaultData.getDefinition().setAttributeType("gender", createGenderHierarchy(records));

        defaultData.getDefinition().setAttributeType("race", AttributeType.QUASI_IDENTIFYING_ATTRIBUTE);
        defaultData.getDefinition().setAttributeType("race", createRaceHierarchy(records));

        defaultData.getDefinition().setAttributeType("city", AttributeType.INSENSITIVE_ATTRIBUTE);
        defaultData.getDefinition().setAttributeType("city", createCityHierarchy(tuples));

        return defaultData;
    }

    private void addRecordToData(AnonymizedRecord record, Data.DefaultData defaultData) {
        logger.debug("Adding: {}, {}, {}, {}, {}", record.getArmed(), record.getAge(), record.getGender(), record.getRace(), record.getCity());
        defaultData.add(record.getArmed(), record.getAge(), record.getGender(), record.getRace(), record.getCity());
    }

    public AttributeType.Hierarchy.DefaultHierarchy createAgeHierarchy(List<AnonymizedRecord> records) {
        var hierarchy = AttributeType.Hierarchy.create();
        
        records.stream()
               .map(AnonymizedRecord::getAge)
               .forEach(age -> hierarchy.add(age, categorizeL1Age(age), categorizeL2Age(age)));

        return hierarchy;
    }

    // 0-10, 11-20, 21-30, 31-40, 41-50, 51-60, 61-70, 71-80, 81-90, 91-100, 100+
    private String categorizeL1Age(String age) {
        var ageNum = Double.parseDouble(age);
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
        
        return result;
    }

    // 0-30, 31-60, 61-90, 90+
    private String categorizeL2Age(String age) {
        var ageNum = Double.parseDouble(age);
        var result = "90+";
        
        if (ageNum <= 30.0) {
            result = "0-30";
        } else if (ageNum <= 60.0) {
            result = "31-60";
        } else if (ageNum <= 90.0) {
            result = "61-90";
        }
        
        logger.debug("Age: {} -> L2 category: {}", ageNum, result);
        
        return result;
    }


    // Genders: F, M
    public AttributeType.Hierarchy.DefaultHierarchy createGenderHierarchy(List<AnonymizedRecord> records) {
        var hierarchy = AttributeType.Hierarchy.create();
        hierarchy.add("M", "Person");
        hierarchy.add("F", "Person");

        return hierarchy;
    }

    // Races: Asian, Black, Hispanic, Native, Other, White
    public AttributeType.Hierarchy.DefaultHierarchy createRaceHierarchy(List<AnonymizedRecord> records) {
        var hierarchy = AttributeType.Hierarchy.create();
        hierarchy.add("Asian", "Any");
        hierarchy.add("Black", "Any");
        hierarchy.add("Hispanic", "Any");
        hierarchy.add("Native", "Any");
        hierarchy.add("Other", "Any");
        hierarchy.add("White", "Any");

        return hierarchy;
    }

    public AttributeType.Hierarchy.DefaultHierarchy createCityHierarchy(List<CityStateTuple> tuples) {
        var statesPerCity = tuples.stream()
                                  .distinct() // remove duplicates
                                  .collect(toMap(CityStateTuple::getCity, CityStateTuple::getState, (a, b) -> a));

        var hierarchy = AttributeType.Hierarchy.create();
        statesPerCity.keySet()
                     .forEach(city -> {
                         logger.debug("Adding: {} -> {} -> \"USA\"", city, statesPerCity.get(city));
                         hierarchy.add(city, statesPerCity.get(city), "USA");
                     });

        return hierarchy;
    }
}
