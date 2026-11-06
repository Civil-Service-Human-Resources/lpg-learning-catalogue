package uk.gov.cslearning.catalogue.client.learnerRecord;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.client.IHttpClient;
import uk.gov.cslearning.catalogue.service.record.model.Event;
import uk.gov.cslearning.catalogue.service.record.model.Booking;
import uk.gov.cslearning.catalogue.exception.NotFoundException;

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
        RequestEntity<Void> requestEntity = RequestEntity.get(String.format(eventUrlFormat, eventId)).build();
        try {
            return httpClient.executeRequest(requestEntity, Event.class);
        } catch (NotFoundException e) {
            return null;
        }
    }

    public List<Event> getBulkEvents(List<String> moduleEventUids, boolean getBookingCount) {
        RequestEntity<Void> requestEntity = RequestEntity.get(String.format(bulkEventsUrl, moduleEventUids, getBookingCount)).build();
        try {
            return httpClient.executeRequest(requestEntity, List.class);
        } catch (NotFoundException e) {
            return null;
        }
    }

    public List<Booking> getBookings(String eventId) {
        RequestEntity<Void> requestEntity = RequestEntity.get(String.format(bookingUrlFormat, eventId)).build();
        try {
            return httpClient.executeRequest(requestEntity, List.class);
        } catch (NotFoundException e) {
            return null;
        }
    }
}
