package uk.gov.cslearning.catalogue.domain.module;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.validation.constraints.NotNull;

public class Venue {
    @NotNull
    private String location;

    private String address;

    private Integer capacity;

    private Integer minCapacity;

    public Venue() {
    }

    public Venue(@NotNull String location) {
        this.location = location;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getMinCapacity() {
        return minCapacity;
    }

    public void setMinCapacity(Integer minCapacity) {
        this.minCapacity = minCapacity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Venue venue = (Venue) o;

        return new EqualsBuilder()
                .append(location, venue.location)
                .append(address, venue.address)
                .append(capacity, venue.capacity)
                .append(minCapacity, venue.minCapacity)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(location)
                .append(address)
                .append(capacity)
                .append(minCapacity)
                .toHashCode();
    }

}
