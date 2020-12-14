package gr.aegean.msc.dbprivacy.core.infra;

import gr.aegean.msc.dbprivacy.core.model.CityStateTuple;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public final class CityStateTupleMapper implements RowMapper<CityStateTuple> {

    @Override
    public CityStateTuple mapRow(final ResultSet rs, final int rowNum) throws SQLException {

        var city = rs.getString("city");
        var state = rs.getString("state");

        return new CityStateTuple(city, state);
    }
}
