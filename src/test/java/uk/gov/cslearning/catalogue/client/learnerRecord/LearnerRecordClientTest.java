package uk.gov.cslearning.catalogue.client.learnerRecord;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.RequestEntity;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.catalogue.client.IHttpClient;
import uk.gov.cslearning.catalogue.exception.NotFoundException;
import uk.gov.cslearning.catalogue.service.record.model.Booking;
import uk.gov.cslearning.catalogue.service.record.model.Event;

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LearnerRecordClientTest {

    @Mock
    private IHttpClient httpClient;

    private final String eventUrlFormat = "/event/%s";
    private final String bulkEventsUrl = "/events-list";
    private final String bookingUrlFormat = "/event/%s/booking";

    private LearnerRecordClient client;

    @BeforeEach
    public void setUp() {
        client = new LearnerRecordClient(httpClient, eventUrlFormat, bulkEventsUrl, bookingUrlFormat);
    }

    @Test
    public void testGetEvent() {
        String eventId = "event-123";
        Event event = mock(Event.class);
        URI uri = UriComponentsBuilder.fromUriString("/event/{eventId}")
                .buildAndExpand(eventId)
                .toUri();
        RequestEntity<Void> requestEntity = RequestEntity.get(uri).build();

        when(httpClient.executeRequest(requestEntity, Event.class)).thenReturn(event);

        Event result = client.getEvent(eventId);

        assertEquals(event, result);
    }

    @Test
    public void testGetEventNotFound() {
        String eventId = "event-123";
        URI uri = UriComponentsBuilder.fromUriString("/event/{eventId}")
                .buildAndExpand(eventId)
                .toUri();
        RequestEntity<Void> requestEntity = RequestEntity.get(uri).build();

        when(httpClient.executeRequest(requestEntity, Event.class)).thenThrow(new NotFoundException("Event not found"));

        Event result = client.getEvent(eventId);

        assertNull(result);
    }

    @Test
    public void testGetBulkEvents() {
        List<String> eventIds = List.of("e1", "e2");
        List<Event> events = List.of(mock(Event.class));
        URI uri = UriComponentsBuilder.fromUriString(bulkEventsUrl)
                .build()
                .toUri();
        RequestEntity<Void> requestEntity = RequestEntity.get(uri).build();

        when(httpClient.executeRequest(requestEntity, List.class)).thenReturn(events);

        List<Event> result = client.getBulkEvents(eventIds, true);

        assertEquals(events, result);
    }

    @Test
    public void testGetBulkEventsNotFound() {
        List<String> eventIds = List.of("e1", "e2");
        URI uri = UriComponentsBuilder.fromUriString(bulkEventsUrl)
                .build()
                .toUri();
        RequestEntity<Void> requestEntity = RequestEntity.get(uri).build();

        when(httpClient.executeRequest(requestEntity, List.class)).thenThrow(new NotFoundException("Not found"));

        List<Event> result = client.getBulkEvents(eventIds, true);

        assertNull(result);
    }

    @Test
    public void testGetBookings() {
        String eventId = "event-123";
        List<Booking> bookings = List.of(mock(Booking.class));
        URI uri = UriComponentsBuilder.fromUriString("/event/{eventId}/booking")
                .buildAndExpand(eventId)
                .toUri();
        RequestEntity<Void> requestEntity = RequestEntity.get(uri).build();

        when(httpClient.executeRequest(requestEntity, List.class)).thenReturn(bookings);

        List<Booking> result = client.getBookings(eventId);

        assertEquals(bookings, result);
    }

    @Test
    public void testGetBookingsNotFound() {
        String eventId = "event-123";
        URI uri = UriComponentsBuilder.fromUriString("/event/{eventId}/booking")
                .buildAndExpand(eventId)
                .toUri();
        RequestEntity<Void> requestEntity = RequestEntity.get(uri).build();

        when(httpClient.executeRequest(requestEntity, List.class)).thenThrow(new NotFoundException("Booking not found"));

        List<Booking> result = client.getBookings(eventId);

        assertNull(result);
    }
}
