package uk.gov.cslearning.catalogue.client.learnerRecord;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.catalogue.client.IHttpClient;
import uk.gov.cslearning.catalogue.service.record.model.Event;
import uk.gov.cslearning.catalogue.service.record.model.Booking;
import uk.gov.cslearning.catalogue.exception.NotFoundException;

import java.net.URI;
import java.util.List;

@Component
public class LearnerRecordClient implements ILearnerRecordClient {

    private final IHttpClient httpClient;
    private final String eventUrlFormat;
    private final String bulkEventsUrl;
    private final String bookingUrlFormat;

    public LearnerRecordClient(@Qualifier("learnerRecordHttpClient")  IHttpClient httpClient,
                               @Value("${record.eventUrlFormat}") String eventUrlFormat,
                               @Value("${record.bulkEventsUrl}") String bulkEventsUrl,
                               @Value("${record.bookingUrlFormat}") String bookingUrlFormat) {
        this.httpClient = httpClient;
        this.eventUrlFormat = eventUrlFormat;
        this.bulkEventsUrl = bulkEventsUrl;
        this.bookingUrlFormat = bookingUrlFormat;
    }

    public Event getEvent(String eventId) {
        String template = eventUrlFormat.contains("%s") ? eventUrlFormat.replace("%s", "{eventId}") : eventUrlFormat;
        URI uri = UriComponentsBuilder.fromUriString(template)
                .buildAndExpand(eventId)
                .toUri();
        RequestEntity<Void> requestEntity = RequestEntity.get(uri).build();
        try {
            return httpClient.executeRequest(requestEntity, Event.class);
        } catch (NotFoundException e) {
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Event> getBulkEvents(List<String> moduleEventUids, boolean getBookingCount) {
        URI uri = UriComponentsBuilder.fromUriString(bulkEventsUrl)
                .build()
                .toUri();
        RequestEntity<Void> requestEntity = RequestEntity.get(uri).build();
        try {
            return httpClient.executeRequest(requestEntity, List.class);
        } catch (NotFoundException e) {
            return null;
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Booking> getBookings(String eventId) {
        String template = bookingUrlFormat.contains("%s") ? bookingUrlFormat.replace("%s", "{eventId}") : bookingUrlFormat;
        URI uri = UriComponentsBuilder.fromUriString(template)
                .buildAndExpand(eventId)
                .toUri();
        RequestEntity<Void> requestEntity = RequestEntity.get(uri).build();
        try {
            return httpClient.executeRequest(requestEntity, List.class);
        } catch (NotFoundException e) {
            return null;
        }
    }
}
