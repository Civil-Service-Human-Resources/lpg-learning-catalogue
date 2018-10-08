package uk.gov.cslearning.catalogue.domain.module;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.Assert.assertEquals;

public class DateRangeTest {
    @Test
    public void shouldEqualDateRangeWithSameVaues() {
        LocalDate date = LocalDate.now();
        LocalTime start = LocalTime.NOON;
        LocalTime end = LocalTime.MIDNIGHT;

        DateRange dateRange1 = new DateRange();
        dateRange1.setDate(date);
        dateRange1.setStartTime(start);
        dateRange1.setEndTime(end);

        DateRange dateRange2 = new DateRange();
        dateRange2.setDate(date);
        dateRange2.setStartTime(start);
        dateRange2.setEndTime(end);

        assertEquals(dateRange1, dateRange2);
    }

    @Test
    public void shouldHaveSameHashcodeAsEqualDateRange() {
        LocalDate date = LocalDate.now();
        LocalTime start = LocalTime.NOON;
        LocalTime end = LocalTime.MIDNIGHT;

        DateRange dateRange1 = new DateRange();
        dateRange1.setDate(date);
        dateRange1.setStartTime(start);
        dateRange1.setEndTime(end);

        DateRange dateRange2 = new DateRange();
        dateRange2.setDate(date);
        dateRange2.setStartTime(start);
        dateRange2.setEndTime(end);

        assertEquals(dateRange1.hashCode(), dateRange2.hashCode());
    }

}