package RoniShlomo_And_NikolYosef;

public class Address {
    private String street;
    private int buildingNumber;
    private String city;
    private String country;

    public Address(String street, int buildingNumber, String city, String country) {
        this.street = street;
        this.buildingNumber = buildingNumber;
        this.city = city;
        this.country = country;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(int buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Street: ").append(street)
                .append(", building number: ").append(buildingNumber)
                .append(", city: ").append(city)
                .append(", country: ").append(country);
        return sb.toString();
    }
}