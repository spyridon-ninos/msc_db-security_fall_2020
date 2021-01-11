package gr.aegean.msc.dbprivacy.core.model;

import java.util.Objects;

public final class AnonymizedRecord {

    private String armed;
    private String age;
    private String gender;
    private String race;
    private String city;

    public String getArmed() {
        return armed;
    }

    public void setArmed(final String armed) {
        this.armed = armed;
    }

    public String getAge() {
        return age;
    }

    public void setAge(final String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(final String gender) {
        this.gender = gender;
    }

    public String getRace() {
        return race;
    }

    public void setRace(final String race) {
        this.race = race;
    }

    public String getCity() {
        return city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public AnonymizedRecord() {
    }

    public AnonymizedRecord(
            final String armed,
            final String age,
            final String gender,
            final String race,
            final String city
    ) {
        this.armed = armed;
        this.age = age;
        this.gender = gender;
        this.race = race;
        this.city = city;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AnonymizedRecord that = (AnonymizedRecord) o;
        return Objects.equals(armed, that.armed) &&
                Objects.equals(age, that.age) &&
                Objects.equals(gender, that.gender) &&
                Objects.equals(race, that.race) &&
                Objects.equals(city, that.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(armed, age, gender, race, city);
    }

    @Override
    public String toString() {
        return  "[age='" + age + '\'' +
                ", gender='" + gender + '\'' +
                ", race='" + race + '\'' +
                ", city='" + city + '\'' +
                ']';
    }

    public boolean isEquivalent(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        final AnonymizedRecord that = (AnonymizedRecord) other;
        return Objects.equals(age, that.age) &&
                Objects.equals(gender, that.gender) &&
                Objects.equals(race, that.race) &&
                Objects.equals(city, that.city);
    }
}
