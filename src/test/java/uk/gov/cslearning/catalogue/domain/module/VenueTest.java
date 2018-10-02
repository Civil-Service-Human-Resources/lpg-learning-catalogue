package uk.gov.cslearning.catalogue.domain.module;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class VenueTest {
    @Test
    public void shouldEqualVenueWithSameVaues() {
        String location = "test-location";
        String address = "test-address";
        int capacity = 12;
        int minCapacity = 6;

        Venue venue1 = new Venue();
        venue1.setLocation(location);
        venue1.setAddress(address);
        venue1.setCapacity(capacity);
        venue1.setMinCapacity(minCapacity);

        Venue venue2 = new Venue();
        venue2.setLocation(location);
        venue2.setAddress(address);
        venue2.setCapacity(capacity);
        venue2.setMinCapacity(minCapacity);

        assertEquals(venue1, venue2);

    }

    @Test
    public void shouldHaveSameHashcodeAsEqualVenue() {
        String location = "test-location";
        String address = "test-address";
        int capacity = 12;
        int minCapacity = 6;

        Venue venue1 = new Venue();
        venue1.setLocation(location);
        venue1.setAddress(address);
        venue1.setCapacity(capacity);
        venue1.setMinCapacity(minCapacity);

        Venue venue2 = new Venue();
        venue2.setLocation(location);
        venue2.setAddress(address);
        venue2.setCapacity(capacity);
        venue2.setMinCapacity(minCapacity);

        assertEquals(venue1.hashCode(), venue2.hashCode());
    }

}