package gr.aegean.msc.dbprivacy.core.business;

import gr.aegean.msc.dbprivacy.core.model.AnonymizedRecord;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.toMap;

public final class LDiversity {

    private final Logger logger = LoggerFactory.getLogger(LDiversity.class);

    public boolean check(long l, Map<Integer, List<AnonymizedRecord>> data) {

        if (l < 1) {
            throw new IllegalArgumentException("l in l-Diversity cannot be less than 1. Aborting.");
        }

        logger.debug("Checking dataset for l-diversity with l: {}", l);

        return data.values()
                   .stream()
                   .parallel()
                   .allMatch(equivClass -> isLDiverse(equivClass, l));
    }

    private boolean isLDiverse(List<AnonymizedRecord> equivalenceClass, long l) {

        logger.debug("Equivalence class: {}", equivalenceClass.get(0));

        var distinctSensitiveValues = equivalenceClass.stream()
                                                      .map(AnonymizedRecord::getArmed)
                                                      .distinct()
                                                      .collect(Collectors.toList());

        logger.debug("Distinct values: {}", distinctSensitiveValues);

        var valueOccurrence = distinctSensitiveValues.stream()
                                                    .collect(toMap(Function.identity(), value -> count(value, equivalenceClass)));

        logger.debug("\nValue occurrences: {}", valueOccurrence);

        return valueOccurrence.values()
                              .stream()
                              .allMatch(value -> {
                                  double occurrence =  value/((double) equivalenceClass.size());
                                  double probability = 1.0/l;
                                  logger.debug("count: {}, ecSize: {}, occurrence: {}, probability: {}", value, equivalenceClass.size(), occurrence, probability);
                                  return occurrence <= probability;
                              });
    }

    private long count(String value, List<AnonymizedRecord> equivalenceClass) {
        return equivalenceClass.stream()
                               .map(AnonymizedRecord::getArmed)
                               .filter(armed -> armed.equals(value))
                               .count();
    }
}
