package uk.gov.cslearning.catalogue.service.record;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.catalogue.domain.module.CancellationReason;
import uk.gov.cslearning.catalogue.domain.module.EventStatus;
import uk.gov.cslearning.catalogue.service.record.model.Booking;
import uk.gov.cslearning.catalogue.service.record.model.Event;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LearnerRecordServiceTest {

    private final RestTemplate restTemplate = Mockito.mock(RestTemplate.class);
    private final RequestEntityFactory requestEntityFactory = Mockito.mock(RequestEntityFactory.class);
    private final String eventUrlFormat = "testHost:9000/event/%s";
    private final String bookingUrlFormat = "testHost:9000/event/%s/booking";
    private final String bulkEventsUrl = "testHost:9000/events-list";

    private final LearnerRecordService learnerRecordService = new LearnerRecordService(restTemplate, requestEntityFactory, eventUrlFormat, bookingUrlFormat, bulkEventsUrl);

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

        RequestEntity<Void> requestEntity = Mockito.mock();
        @SuppressWarnings("unchecked")
        ResponseEntity<List<Booking>> responseEntity = (ResponseEntity<List<Booking>>) Mockito.mock(ResponseEntity.class);

        URI uri = UriComponentsBuilder.fromUriString(String.format(bookingUrlFormat, eventId)).build().toUri();

        Mockito.when(requestEntityFactory.createGetRequest(uri)).thenReturn(requestEntity);
        Mockito.when(restTemplate.exchange(requestEntity, new ParameterizedTypeReference<List<Booking>>(){})).thenReturn(responseEntity);
        Mockito.when(responseEntity.getBody()).thenReturn(bookings);

        Assert.assertEquals(learnerRecordService.getEventBookings(eventId), bookings);
    }

    @Test
    public void shouldReturnNullOnGetEventBookingsException(){
        String eventId = "eventId";
        URI uri = UriComponentsBuilder.fromUriString(String.format(bookingUrlFormat, eventId)).build().toUri();
        Mockito.when(requestEntityFactory.createGetRequest(uri)).thenThrow(new RequestEntityException(new Exception("error")));

        Assert.assertNull(learnerRecordService.getEventBookings(eventId));
    }

    @Test
    public void shouldReturnEventStatus(){
        String eventId = "eventId";
        Event event = new Event();
        event.setStatus("Active");

        RequestEntity<Void> requestEntity = Mockito.mock();
        @SuppressWarnings("unchecked")
        ResponseEntity<Event> responseEntity = (ResponseEntity<Event>) Mockito.mock(ResponseEntity.class);

        URI uri = UriComponentsBuilder.fromUriString(String.format(eventUrlFormat, eventId)).build().toUri();

        Mockito.when(requestEntityFactory.createGetRequest(uri)).thenReturn(requestEntity);
        Mockito.when(restTemplate.exchange(requestEntity, Event.class)).thenReturn(responseEntity);
        Mockito.when(responseEntity.getBody()).thenReturn(event);

        Assert.assertEquals(EventStatus.ACTIVE, learnerRecordService.getEventStatus(eventId));
    }

    @Test
    public void shouldReturnNullOnGetEventStatusException(){
        String eventId = "eventId";
        URI uri = UriComponentsBuilder.fromUriString(String.format(eventUrlFormat, eventId)).build().toUri();

        Mockito.when(requestEntityFactory.createGetRequest(uri)).thenThrow(new RestClientException("error"));

        Assert.assertNull(learnerRecordService.getEventStatus(eventId));
    }

    @Test
    public void shouldReturnCancellationReason(){
        String eventId = "eventId";
        Event event = new Event();
        event.setCancellationReason("Unavailable");

        RequestEntity<Void> requestEntity = Mockito.mock();
        @SuppressWarnings("unchecked")
        ResponseEntity<Event> responseEntity = (ResponseEntity<Event>) Mockito.mock(ResponseEntity.class);

        URI uri = UriComponentsBuilder.fromUriString(String.format(eventUrlFormat, eventId)).build().toUri();

        Mockito.when(requestEntityFactory.createGetRequest(uri)).thenReturn(requestEntity);
        Mockito.when(restTemplate.exchange(requestEntity, Event.class)).thenReturn(responseEntity);
        Mockito.when(responseEntity.getBody()).thenReturn(event);

        Assert.assertEquals(CancellationReason.UNAVAILABLE, learnerRecordService.getCancellationReason(eventId));
    }

    @Test
    public void shouldReturnNullOnGetCancellationReasonException(){
        String eventId = "eventId";
        URI uri = UriComponentsBuilder.fromUriString(String.format(eventUrlFormat, eventId)).build().toUri();

        Mockito.when(requestEntityFactory.createGetRequest(uri)).thenThrow(new RestClientException("error"));

        Assert.assertNull(learnerRecordService.getCancellationReason(eventId));
    }

    @Test
    public void shouldReturnEventActiveBookingsCount(){
        String eventId = "eventId";
        Event event = new Event();
        event.setActiveBookingCount(5);

        RequestEntity<Void> requestEntity = Mockito.mock();
        @SuppressWarnings("unchecked")
        ResponseEntity<Event> responseEntity = (ResponseEntity<Event>) Mockito.mock(ResponseEntity.class);

        URI uri = UriComponentsBuilder.fromUriString(String.format(eventUrlFormat, eventId))
                .queryParam("getBookingCount", true)
                .build().toUri();

        Mockito.when(requestEntityFactory.createGetRequest(uri)).thenReturn(requestEntity);
        Mockito.when(restTemplate.exchange(requestEntity, Event.class)).thenReturn(responseEntity);
        Mockito.when(responseEntity.getBody()).thenReturn(event);

        Assert.assertEquals(Integer.valueOf(5), learnerRecordService.getEventActiveBookingsCount(eventId));
    }

    @Test
    public void shouldReturnEventsList(){
        List<String> uids = Arrays.asList("uid1", "uid2");
        Event event1 = new Event();
        Event event2 = new Event();
        List<Event> events = Arrays.asList(event1, event2);

        RequestEntity<Void> requestEntity = Mockito.mock();
        @SuppressWarnings("unchecked")
        ResponseEntity<List<Event>> responseEntity = (ResponseEntity<List<Event>>) Mockito.mock(ResponseEntity.class);

        URI uri = UriComponentsBuilder.fromUriString(bulkEventsUrl)
                .queryParam("uids", uids.toArray())
                .queryParam("getBookingCount", false)
                .build().toUri();

        Mockito.when(requestEntityFactory.createGetRequest(uri)).thenReturn(requestEntity);
        Mockito.when(restTemplate.exchange(requestEntity, new ParameterizedTypeReference<List<Event>>(){})).thenReturn(responseEntity);
        Mockito.when(responseEntity.getBody()).thenReturn(events);

        Assert.assertEquals(events, learnerRecordService.getEvents(uids, false));
    }
}
