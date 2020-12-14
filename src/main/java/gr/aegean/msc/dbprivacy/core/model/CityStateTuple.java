package gr.aegean.msc.dbprivacy.core.model;

import java.util.Comparator;
import java.util.Objects;

public final class CityStateTuple implements Comparable<CityStateTuple>{

    private String city;
    private String state;

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public CityStateTuple() {
    }

    public CityStateTuple(final String city, final String state) {
        this.city = city;
        this.state = state;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CityStateTuple that = (CityStateTuple) o;
        return Objects.equals(city, that.city) &&
                Objects.equals(state, that.state);
    }

    @Override
    public int hashCode() {
        return Objects.hash(city, state);
    }

    @Override
    public String toString() {
        return "CityStateTuple{" +
                "city='" + city + '\'' +
                ", state='" + state + '\'' +
                '}';
    }

    @Override
    public int compareTo(final CityStateTuple other) {
        return Comparator.comparing(CityStateTuple::getState)
                         .thenComparing(CityStateTuple::getCity)
                         .compare(this, other);
    }
}
