package gr.aegean.msc.dbprivacy.core.infra;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;


@Component
public final class RecordsPerArmedMapper implements RowMapper<Map.Entry<String, Integer>> {

    @Override
    public Map.Entry<String, Integer> mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        var armed = rs.getString("armed");
        var numOfRecords = rs.getInt("numOfRecords");

        return Map.of(armed, numOfRecords)
                  .entrySet()
                  .stream()
                  .findAny()
                  .get(); // no need to check for NPE
    }
}
