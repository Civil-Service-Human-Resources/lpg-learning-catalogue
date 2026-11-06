package uk.gov.cslearning.catalogue.service.record;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import uk.gov.cslearning.catalogue.client.learnerRecord.ILearnerRecordClient;
import uk.gov.cslearning.catalogue.domain.module.CancellationReason;
import uk.gov.cslearning.catalogue.domain.module.EventStatus;
import uk.gov.cslearning.catalogue.service.record.model.Booking;
import uk.gov.cslearning.catalogue.service.record.model.Event;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class LearnerRecordService {

    private final ILearnerRecordClient client;

    public LearnerRecordService(ILearnerRecordClient client) {
        this.client = client;
    }

    public Integer getEventActiveBookingsCount(String eventId) {
        try {
            Integer count = 0;
            Event event = client.getEvent(eventId);
            if (event != null) {
                count = event.getActiveBookingCount();
            }
            return count;
        } catch (RequestEntityException | RestClientException e) {
            log.error("Could not get booking count from learner record: {}", e.getLocalizedMessage());
            return 0;
        }
    }

    public List<Booking> getEventBookings(String eventId) {
        try{
            return client.getBookings(eventId);
        } catch (RequestEntityException | RestClientException e) {
            log.error("Could not get bookings from learner record: {}", e.getLocalizedMessage());
            return null;
        }
    }

    public EventStatus getEventStatus(String eventId) {
        try {
            Event event = client.getEvent(eventId);
            assert event != null;
            return EventStatus.forValue(event.getStatus());
        } catch (RequestEntityException | RestClientException e) {
            log.error("Could not get event from learner record: {}", e.getLocalizedMessage());
            return null;
        }
    }

    public CancellationReason getCancellationReason(String eventId) {
        try {
            Event event = client.getEvent(eventId);
            assert event != null;
            return CancellationReason.forValue(event.getCancellationReason());
        } catch (RequestEntityException | RestClientException e) {
            log.error("Could not get event from learner record: {}", e.getLocalizedMessage());
            return null;
        }
    }

    public List<Event> getEvents(List<String> moduleEventUids, boolean getBookingCount) {
        List<Event> events = new ArrayList<>();
        List<List<String>> batchedUids = Lists.partition(moduleEventUids, 20);
        try {
            batchedUids.forEach(batch -> {
                List<Event> eventsFromUids = client.getBulkEvents(batch, getBookingCount);
                if (eventsFromUids != null) {
                    events.addAll(eventsFromUids);
                }
            });
        } catch (RequestEntityException | RestClientException e) {
            log.error("Could not get events from learner record: {}", e.getLocalizedMessage());
            return null;
        }
        return events;
    }
}
