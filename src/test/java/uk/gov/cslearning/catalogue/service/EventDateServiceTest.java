package uk.gov.cslearning.catalogue.service;

import org.junit.Before;
import org.junit.Test;
import uk.gov.cslearning.catalogue.domain.module.DateRange;
import uk.gov.cslearning.catalogue.domain.module.Event;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EventDateServiceTest {

    private EventDateService eventDateService;

    @Before
    public void setUp() {
        eventDateService = new EventDateService();
    }

    @Test
    public void shouldReturnDateRangesOrderedAsc() {
        DateRange dateRange1 = new DateRange();
        dateRange1.setDate(LocalDate.now().minusDays(10));
        DateRange dateRange2 = new DateRange();
        dateRange2.setDate(LocalDate.now().minusDays(5));
        DateRange dateRange3 = new DateRange();
        dateRange3.setDate(LocalDate.now().minusDays(20));
        DateRange dateRange4 = new DateRange();
        dateRange4.setDate(LocalDate.now().minusDays(15));

        List<DateRange> dateRanges = Arrays.asList(dateRange1, dateRange2, dateRange3, dateRange4);

        Event event = new Event();
        event.setDateRanges(dateRanges);
        String eventId = "event-id";
        event.setId(eventId);

        assertTrue(eventDateService.getFirstDateChronologically(event).isPresent());
        assertEquals(dateRange3, eventDateService.getFirstDateChronologically(event).get());
    }
}
