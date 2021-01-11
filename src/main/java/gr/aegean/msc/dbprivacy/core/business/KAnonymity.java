package gr.aegean.msc.dbprivacy.core.business;

import gr.aegean.msc.dbprivacy.core.model.AnonymizedRecord;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static gr.aegean.msc.dbprivacy.core.business.Utils.copy;

public final class KAnonymity {

    private final Logger logger = LoggerFactory.getLogger(KAnonymity.class);

    private final List<AnonymizedRecord> anonymizedRecords;
    private final GeneralizationBuilder generalizationBuilder;

    private Map<Integer, List<AnonymizedRecord>> kAnonymizedData;
    private boolean hasStartedProcessing = false;

    public Map<Integer, List<AnonymizedRecord>> getkAnonymizedData() {
        return Optional.ofNullable(kAnonymizedData)
                       .map(Collections::unmodifiableMap)
                       .orElse(Collections.emptyMap());
    }

    public KAnonymity(List<AnonymizedRecord> anonymizedRecords, GeneralizationBuilder generalizationBuilder) {
        this.anonymizedRecords = anonymizedRecords;
        this.generalizationBuilder = generalizationBuilder;
    }

    /**
     * checks if the provided anonymized record list satisfies k-anonymity
     *
     * @param k the equivalence class size
     *
     * @return true if it satisfies, false if not
     */
    public boolean check(int k) {

        logger.debug("Checking k-anonymity for k = {}", k);

        if (k < 1) {
            throw new IllegalArgumentException("k-Anonymity Equivalence Class size cannot be less than 1. Aborting.");
        }

        var copiedAnonymizedRecords = copy(anonymizedRecords);
        do {
            if (hasStartedProcessing) {
                logger.debug("Generalizing the dataset");
                generalizationBuilder.increaseGeneralizationDomain(copiedAnonymizedRecords);
                logger.debug("Generalized dataset: {}", copiedAnonymizedRecords);
            }

            kAnonymizedData = splitToEquivalenceClasses(copiedAnonymizedRecords);

            if (satisfiesAnonymity(k)) {
                logger.warn("It satisfies {}-anonymity", k);
                return true;
            }

            logger.warn("It does not satisfy {}-anonymity", k);
        } while(generalizationBuilder.isNotFullySuppressed());

        return false;
    }

    private Map<Integer, List<AnonymizedRecord>> splitToEquivalenceClasses(List<AnonymizedRecord> anonymizedRecords) {
        logger.debug("Splitting the dataset to equivalence classes");
        hasStartedProcessing = true;
        kAnonymizedData = new HashMap<>();
        anonymizedRecords.forEach(record -> assignToEquivalenceClass(record, kAnonymizedData));
        logger.debug("Result: {}", kAnonymizedData);
        return kAnonymizedData;
    }

    private void assignToEquivalenceClass(AnonymizedRecord record, Map<Integer, List<AnonymizedRecord>> kAnonymizedData) {

        if (kAnonymizedData.keySet().isEmpty()) {

            var equivalenceClass = new ArrayList<AnonymizedRecord>();
            equivalenceClass.add(record);
            kAnonymizedData.put(1, equivalenceClass);

        } else {

            var equivalenceClass = kAnonymizedData.keySet()
                                                  .stream()
                                                  .map(kAnonymizedData::get)
                                                  .filter(equivClass -> record.isEquivalent(equivClass.get(0)))
                                                  .findAny()
                                                  .orElse(new ArrayList<>());

            if (equivalenceClass.isEmpty()) {
                int classNumber = kAnonymizedData.keySet().size() + 1;
                kAnonymizedData.put(classNumber, equivalenceClass);
            }

            equivalenceClass.add(record);
        }
    }

    private boolean satisfiesAnonymity(int ecSize) {
        logger.warn("\n===================================================================");
        getkAnonymizedData().values()
                            .forEach(equivClass -> logger.warn("class size: {}, QI values: {}", equivClass.size(), equivClass.get(0)));

        return getkAnonymizedData().values()
                                   .stream()
                                   .allMatch(equivClass -> equivClass.size() >= ecSize);
    }

    public double getInformationLoss() {
        return generalizationBuilder.getInformationLoss();
    }
}
