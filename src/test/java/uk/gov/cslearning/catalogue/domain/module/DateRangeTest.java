package uk.gov.cslearning.catalogue.domain.module;

import org.junit.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.Assert.assertEquals;

public class DateRangeTest {
    @Test
    public void whenTwoDateRangesHaveTheSameStartAndEndDateTimes_thenTheyAreEqualAndHaveSameHashCodes() {
        Instant startDateTime = Instant.now();
        Instant endDateTime = Instant.now().plus(1, ChronoUnit.HOURS);

        DateRange dateRange1 = new DateRange();
        dateRange1.setStartDateTime(startDateTime);
        dateRange1.setEndDateTime(endDateTime);

        DateRange dateRange2 = new DateRange();
        dateRange2.setStartDateTime(startDateTime);
        dateRange2.setEndDateTime(endDateTime);

        assertEquals(dateRange1, dateRange2);
        assertEquals(dateRange1.hashCode(), dateRange2.hashCode());
    }
}