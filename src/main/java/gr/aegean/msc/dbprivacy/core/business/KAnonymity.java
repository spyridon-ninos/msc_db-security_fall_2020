package gr.aegean.msc.dbprivacy.core.business;

import gr.aegean.msc.dbprivacy.core.model.AnonymizedRecord;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static gr.aegean.msc.dbprivacy.core.business.Utils.copy;

public final class KAnonymity {

    private final Logger logger = LoggerFactory.getLogger(KAnonymity.class);

    private final List<AnonymizedRecord> anonymizedRecords;
    private final GeneralizationBuilder generalizationBuilder;
    private final LDiversity lDiversity;

    private Map<Integer, List<AnonymizedRecord>> kAnonymizedData;
    private List<AnonymizedRecord> modifiedAnonymizedRecords;
    private int roundToModifySet = -1;

    public Map<Integer, List<AnonymizedRecord>> getkAnonymizedData() {
        return Optional.ofNullable(kAnonymizedData)
                       .map(Collections::unmodifiableMap)
                       .orElse(Collections.emptyMap());
    }

    public void setRoundToModifySet(int round) {
        roundToModifySet = round;
    }

    public List<AnonymizedRecord> getModifiedAnonymizedRecords() {
        return Optional.ofNullable(modifiedAnonymizedRecords)
                       .map(records -> records.stream().map(AnonymizedRecord::new).collect(Collectors.toList()))
                       .orElse(Collections.emptyList());
    }

    public KAnonymity(List<AnonymizedRecord> anonymizedRecords, GeneralizationBuilder generalizationBuilder) {
        this(anonymizedRecords, generalizationBuilder, null);
    }

    public KAnonymity(
            List<AnonymizedRecord> anonymizedRecords,
            GeneralizationBuilder generalizationBuilder,
            LDiversity lDiversity
    ) {
        this.anonymizedRecords = anonymizedRecords;
        this.generalizationBuilder = generalizationBuilder;
        this.lDiversity = lDiversity;
    }


    /**
     * checks if the provided anonymized record list satisfies k-anonymity
     *
     * @param k the equivalence class size
     *
     * @return true if it satisfies, false if not
     */
    public boolean check(int k) {

        logger.warn("======================================");
        logger.warn("=== Checking k-anonymity for k = {} ===", k);

        if (k < 1) {
            throw new IllegalArgumentException("k-Anonymity Equivalence Class size cannot be less than 1. Aborting.");
        }

        int round = 1;
        var copiedAnonymizedRecords = copy(anonymizedRecords);
        generalizationBuilder.reset();
        boolean hasStartedProcessing = false;

        do {
            logger.warn("======================================");
            logger.warn("Round {}:", round);
            if (hasStartedProcessing) {
                logger.warn("Generalizing the dataset");
                generalizationBuilder.increaseGeneralizationDomain(copiedAnonymizedRecords);
                logger.debug("Generalized dataset: {}", copiedAnonymizedRecords);
            }

            logger.warn("Generalization levels:\n{}", generalizationBuilder.getCurrentGeneralizationLevels());

            kAnonymizedData = splitToEquivalenceClasses(copiedAnonymizedRecords);

            logger.warn("Dataset split to {} equivalent classes", kAnonymizedData.entrySet().size());

            long numOfEquivClassWithOne = kAnonymizedData.values()
                                                         .stream()
                                                         .map(List::size)
                                                         .filter(size -> size == 1)
                                                         .count();

            long numOfEquivClassWithMore = kAnonymizedData.values()
                                                          .stream()
                                                          .map(List::size)
                                                          .filter(size -> size > 1)
                                                          .count();

            logger.warn("{} classes with 1 element, {} with more than one elements", numOfEquivClassWithOne, numOfEquivClassWithMore);

            String infoLoss = String.format("%,4.2f%%", generalizationBuilder.getInformationLoss()*100);
            logger.warn("Current information loss: {}", infoLoss);

            if (satisfiesAnonymity(k)) {
                logger.warn("\nIt satisfies {}-anonymity\n", k);

                if (lDiversity != null) {
                    for (int l=1; l<=k; l++) {
                        logger.warn("Checking for l-Diversity with l = {}: {}", l, lDiversity.check(l, kAnonymizedData));
                    }
                }

                return true;
            }

            logger.warn("\nIt does not satisfy {}-anonymity\n", k);

            if (roundToModifySet == round) {
                logger.warn("Getting a modified set");
                modifySet(kAnonymizedData);
            }

            round++;
            hasStartedProcessing = true;
        } while(generalizationBuilder.isNotFullySuppressed());

        return false;
    }

    private Map<Integer, List<AnonymizedRecord>> splitToEquivalenceClasses(List<AnonymizedRecord> anonymizedRecords) {
        logger.debug("Splitting the dataset to equivalence classes");
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
        getkAnonymizedData().values()
                            .forEach(equivClass -> logger.debug("class size: {}, QI values: {}", equivClass.size(), equivClass.get(0)));

        return getkAnonymizedData().values()
                                   .stream()
                                   .allMatch(equivClass -> equivClass.size() >= ecSize);
    }

    public double getInformationLoss() {
        return generalizationBuilder.getInformationLoss();
    }

    private void modifySet(Map<Integer, List<AnonymizedRecord>> kAnonymizedData) {
        var uniqueRecords = kAnonymizedData.values()
                                           .stream()
                                           .filter(list -> list.size() == 1)
                                           .flatMap(List::stream)
                                           .collect(Collectors.toList());

        logger.warn("Unique records size: {}", uniqueRecords.size());

        var modifiedSet = anonymizedRecords.stream()
                                           .filter(record -> !shouldDrop(record, uniqueRecords))
                                           .collect(Collectors.toList());

        logger.warn("Modified set size: {}", modifiedSet.size());

        modifiedAnonymizedRecords = modifiedSet;
    }

    private boolean shouldDrop(AnonymizedRecord record, List<AnonymizedRecord> uniqueRecords) {
        return uniqueRecords.stream()
                            .anyMatch(rec -> rec.getId() == record.getId());
    }
}
