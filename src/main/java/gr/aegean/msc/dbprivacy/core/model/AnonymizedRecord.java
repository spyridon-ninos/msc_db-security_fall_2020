package gr.aegean.msc.dbprivacy.core.model;

import java.util.Objects;

public final class AnonymizedRecord {

    private long id;
    private String armed;
    private String age;
    private String gender;
    private String race;
    private String city;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public AnonymizedRecord(AnonymizedRecord old) {
        this(old.getId(), old.getArmed(), old.getAge(), old.getGender(), old.getRace(), old.getCity());
    }

    public AnonymizedRecord(
            final long id,
            final String armed,
            final String age,
            final String gender,
            final String race,
            final String city
    ) {
        this.id = id;
        this.armed = armed;
        this.age = age;
        this.gender = gender;
        this.race = race;
        this.city = city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnonymizedRecord that = (AnonymizedRecord) o;
        return id == that.id && Objects.equals(armed, that.armed) && Objects.equals(age, that.age) && Objects.equals(gender, that.gender) && Objects.equals(race, that.race) && Objects.equals(city, that.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, armed, age, gender, race, city);
    }

    @Override
    public String toString() {
        return  "[id='" + id + '\'' +
                ", age='" + age + '\'' +
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
