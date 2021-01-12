package gr.aegean.msc.dbprivacy.core.infra;

import gr.aegean.msc.dbprivacy.core.model.AnonymizedRecord;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public final class AnonymizedRecordMapper implements RowMapper<AnonymizedRecord> {

    @Override
    public AnonymizedRecord mapRow(final ResultSet rs, final int rowNum) throws SQLException {

        var id = rs.getInt("id");
        var armed = rs.getString("armed");
        var age = rs.getString("age");
        var gender = rs.getString("gender");
        var race = rs.getString("race");
        var city = rs.getString("city");

        return new AnonymizedRecord(id, armed, age, gender, race, city);
    }
}
