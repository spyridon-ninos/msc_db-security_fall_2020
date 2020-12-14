package gr.aegean.msc.dbprivacy.core.business;

import org.deidentifier.arx.ARXAnonymizer;
import org.deidentifier.arx.ARXConfiguration;
import org.deidentifier.arx.ARXLattice;
import org.deidentifier.arx.ARXResult;
import org.deidentifier.arx.Data;
import org.deidentifier.arx.criteria.DistinctLDiversity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.stream.IntStream;

@Component
public final class KAnonymity {

    private final Logger logger = LoggerFactory.getLogger(KAnonymity.class);

    public void run(Data.DefaultData data) {
        IntStream.range(1, 11)
                 .forEach(k -> checkKAnonymity(data, k));
    }

    private void checkKAnonymity(Data.DefaultData data, int k) {

        ARXConfiguration config = ARXConfiguration.create();
        config.addPrivacyModel(new org.deidentifier.arx.criteria.KAnonymity(k));
        config.addPrivacyModel(new DistinctLDiversity("armed", 1));

        ARXAnonymizer anonymizer = new ARXAnonymizer();
        ARXResult result;
        try {
            result = anonymizer.anonymize(data, config);
            data.getHandle().release(); // need this for every new run we perform
        } catch (IOException ex) {
            logger.error("Received exception: {}", ex.getMessage());
            logger.debug("{}", ex.getMessage(), ex);
            return;
        }

        ARXLattice.ARXNode optimum = result.getGlobalOptimum();
        if (optimum == null) {
            logger.warn("No solution found for k={}", k);
        } else {
            int totalGeneralizationLevel = optimum.getTotalGeneralizationLevel();
            logger.warn("Solution found for k={}. Information loss min: {}%, max: {}%. Total Generalization level: {}", k, optimum.getLowestScore(), optimum.getHighestScore(), totalGeneralizationLevel);
        }
    }
}
