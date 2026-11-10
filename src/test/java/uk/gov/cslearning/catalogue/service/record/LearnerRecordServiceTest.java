package uk.gov.cslearning.catalogue.service.record;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestClientException;
import uk.gov.cslearning.catalogue.client.learnerRecord.ILearnerRecordClient;
import uk.gov.cslearning.catalogue.client.learnerRecord.LearnerRecordClient;
import uk.gov.cslearning.catalogue.domain.module.CancellationReason;
import uk.gov.cslearning.catalogue.domain.module.EventStatus;
import uk.gov.cslearning.catalogue.service.record.model.Booking;
import uk.gov.cslearning.catalogue.service.record.model.Event;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LearnerRecordServiceTest {

    private final ILearnerRecordClient client =  Mockito.mock(LearnerRecordClient.class);

    private final LearnerRecordService learnerRecordService = new LearnerRecordService(client);

    @Test
    public void shouldReturnEventBookings(){
        String eventId = "eventId";

        List<Booking> bookings = new ArrayList<>();

        Booking booking1 = new Booking();
        Booking booking2 = new Booking();

        booking1.setId(1);
        booking2.setId(2);

        bookings.add(booking1);
        bookings.add(booking2);

        Mockito.when(client.getBookings(eventId)).thenReturn(bookings);

        Assertions.assertEquals(learnerRecordService.getEventBookings(eventId), bookings);
    }

    @Test
    public void shouldReturnNullOnGetEventBookingsException(){
        String eventId = "eventId";
        Mockito.when(client.getBookings(eventId)).thenThrow(new RestClientException("error"));

        Assertions.assertNull(learnerRecordService.getEventBookings(eventId));
    }

    @Test
    public void shouldReturnEventStatus(){
        String eventId = "eventId";
        Event event = new Event();
        event.setStatus("Active");

        Mockito.when(client.getEvent(eventId)).thenReturn(event);

        Assertions.assertEquals(EventStatus.ACTIVE, learnerRecordService.getEventStatus(eventId));
    }

    @Test
    public void shouldReturnNullOnGetEventStatusException(){
        String eventId = "eventId";
        Mockito.when(client.getEvent(eventId)).thenThrow(new RestClientException("error"));

        Assertions.assertNull(learnerRecordService.getEventStatus(eventId));
    }

    @Test
    public void shouldReturnNullOnGetEventStatusWhenEventIsNull(){
        String eventId = "eventId";
        Mockito.when(client.getEvent(eventId)).thenReturn(null);

        Assertions.assertNull(learnerRecordService.getEventStatus(eventId));
    }

    @Test
    public void shouldReturnNullOnGetEventStatusWhenStatusIsNull(){
        String eventId = "eventId";
        Event event = new Event();
        event.setStatus(null);
        Mockito.when(client.getEvent(eventId)).thenReturn(event);

        Assertions.assertNull(learnerRecordService.getEventStatus(eventId));
    }

    @Test
    public void shouldReturnCancellationReason(){
        String eventId = "eventId";
        Event event = new Event();
        event.setCancellationReason("Unavailable");

        Mockito.when(client.getEvent(eventId)).thenReturn(event);

        Assertions.assertEquals(CancellationReason.UNAVAILABLE, learnerRecordService.getCancellationReason(eventId));
    }

    @Test
    public void shouldReturnNullOnGetCancellationReasonException(){
        String eventId = "eventId";

        Mockito.when(client.getEvent(eventId)).thenThrow(new RestClientException("error"));

        Assertions.assertNull(learnerRecordService.getCancellationReason(eventId));
    }

    @Test
    public void shouldReturnNullOnGetCancellationReasonWhenEventIsNull(){
        String eventId = "eventId";
        Mockito.when(client.getEvent(eventId)).thenReturn(null);

        Assertions.assertNull(learnerRecordService.getCancellationReason(eventId));
    }

    @Test
    public void shouldReturnNullOnGetCancellationReasonWhenReasonIsNull(){
        String eventId = "eventId";
        Event event = new Event();
        event.setCancellationReason(null);
        Mockito.when(client.getEvent(eventId)).thenReturn(event);

        Assertions.assertNull(learnerRecordService.getCancellationReason(eventId));
    }

    @Test
    public void shouldReturnEventActiveBookingsCount(){
        String eventId = "eventId";
        Event event = new Event();
        event.setActiveBookingCount(5);

        Mockito.when(client.getEvent(eventId)).thenReturn(event);

        Assertions.assertEquals(Integer.valueOf(5), learnerRecordService.getEventActiveBookingsCount(eventId));
    }

    @Test
    public void shouldReturnEventsList(){
        List<String> uids = Arrays.asList("uid1", "uid2");
        Event event1 = new Event();
        Event event2 = new Event();
        List<Event> events = Arrays.asList(event1, event2);

        Mockito.when(client.getBulkEvents(uids, false)).thenReturn(events);

        Assertions.assertEquals(events, learnerRecordService.getEvents(uids, false));
    }

    @Test
    public void shouldReturnNullOnGetEventsException(){
        List<String> uids = Arrays.asList("uid1", "uid2");
        Mockito.when(client.getBulkEvents(uids, false)).thenThrow(new RuntimeException("error"));

        Assertions.assertNull(learnerRecordService.getEvents(uids, false));
    }
}
