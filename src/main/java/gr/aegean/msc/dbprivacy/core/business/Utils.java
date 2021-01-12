package gr.aegean.msc.dbprivacy.core.business;

import gr.aegean.msc.dbprivacy.core.model.AnonymizedRecord;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public final class Utils {

    public static List<AnonymizedRecord> copy(List<AnonymizedRecord> records) {
        if (records == null || records.isEmpty()) {
            return Collections.emptyList();
        }

        return records.stream()
                      .map(Utils::copy)
                      .collect(Collectors.toList());
    }

    private static AnonymizedRecord copy(AnonymizedRecord record) {
        var copy = new AnonymizedRecord();
        copy.setId(record.getId());
        copy.setGender(record.getGender());
        copy.setRace(record.getRace());
        copy.setCity(record.getCity());
        copy.setAge(record.getAge());
        copy.setArmed(record.getArmed());

        return copy;
    }
}
