package uk.gov.cslearning.catalogue.service.record;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.catalogue.domain.module.CancellationReason;
import uk.gov.cslearning.catalogue.domain.module.EventStatus;
import uk.gov.cslearning.catalogue.service.record.model.Booking;
import uk.gov.cslearning.catalogue.service.record.model.Event;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class LearnerRecordService {

    private final RestTemplate restTemplate;

    private final RequestEntityFactory requestEntityFactory;

    private final String eventUrlFormat;

    private final String bookingUrlFormat;

    private final String bulkEventsUrl;

    public LearnerRecordService(RestTemplate restTemplate, RequestEntityFactory requestEntityFactory,
                                @Value("${record.eventUrlFormat}") String eventUrlFormat,
                                @Value("${record.bookingUrlFormat}") String bookingUrlFormat,
                                @Value("${record.bulkEventsUrl}") String bulkEventsUrl){
        this.restTemplate = restTemplate;
        this.requestEntityFactory = requestEntityFactory;
        this.eventUrlFormat = eventUrlFormat;
        this.bookingUrlFormat = bookingUrlFormat;
        this.bulkEventsUrl = bulkEventsUrl;
    }

    public Integer getEventActiveBookingsCount(String eventId) {
        try{
            Integer count = 0;
            URI uri = UriComponentsBuilder.fromHttpUrl(String.format(eventUrlFormat, eventId))
                    .queryParam("getBookingCount", true)
                    .build().toUri();
            RequestEntity request = requestEntityFactory.createGetRequest(uri);
            Event event = restTemplate.exchange(request, Event.class).getBody();
            if (event != null) {
                count = event.getActiveBookingCount();
            }
            return count;
        } catch (RequestEntityException | RestClientException e) {
            log.error(String.format("Could not get booking count from learner record: %s", e.getLocalizedMessage()));
            return 0;
        }
    }

    public List<Booking> getEventBookings(String eventId) {
        try{
            RequestEntity requestEntity = requestEntityFactory.createGetRequest(String.format(bookingUrlFormat, eventId));
            ResponseEntity<List<Booking>> responseEntity = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<List<Booking>>(){});
            return responseEntity.getBody();
        } catch (RequestEntityException | RestClientException e) {
            log.error("Could not get bookings from learner record: ", e.getLocalizedMessage());
            return null;
        }
    }

    public EventStatus getEventStatus(String eventId) {
        try{
            RequestEntity requestEntity = requestEntityFactory.createGetRequest(String.format(eventUrlFormat, eventId));
            ResponseEntity<Event> responseEntity = restTemplate.exchange(requestEntity, Event.class);

            Event event = responseEntity.getBody();
            return EventStatus.forValue(event.getStatus());
        } catch (RequestEntityException | RestClientException e) {
            log.error("Could not get event from learner record: ", e.getLocalizedMessage());
            return null;
        }
    }

    public CancellationReason getCancellationReason(String eventId) {
        try{
            RequestEntity requestEntity = requestEntityFactory.createGetRequest(String.format(eventUrlFormat, eventId));
            ResponseEntity<Event> responseEntity = restTemplate.exchange(requestEntity, Event.class);

            Event event = responseEntity.getBody();
            return CancellationReason.forValue(event.getCancellationReason());
        } catch (RequestEntityException | RestClientException e) {
            log.error("Could not get event from learner record: %s", e.getLocalizedMessage());
            return null;
        }
    }

    public List<Event> getEvents(List<String> moduleEventUids, boolean getBookingCount) {
        List<Event> events = new ArrayList<>();
        List<List<String>> batchedUids = Lists.partition(moduleEventUids, 20);
        try {
            batchedUids.forEach(batch -> {
                URI uri = UriComponentsBuilder.fromHttpUrl(bulkEventsUrl)
                        .queryParam("uids", batch)
                        .queryParam("getBookingCount", getBookingCount)
                        .build().toUri();
                RequestEntity requestEntity = requestEntityFactory.createGetRequest(uri);
                List<Event> eventsFromUids = restTemplate.exchange(requestEntity, new ParameterizedTypeReference<List<Event>>(){}).getBody();
                if (eventsFromUids != null) {
                    events.addAll(eventsFromUids);
                }
            });
        } catch (RequestEntityException | RestClientException e) {
            log.error("Could not get events from learner record: %s", e.getLocalizedMessage());
            return null;
        }
        return events;
    }
}
