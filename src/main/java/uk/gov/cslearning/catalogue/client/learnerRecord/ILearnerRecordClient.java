package uk.gov.cslearning.catalogue.client.learnerRecord;

import uk.gov.cslearning.catalogue.service.record.model.Event;
import uk.gov.cslearning.catalogue.service.record.model.Booking;

import java.util.List;

public interface ILearnerRecordClient {

    Event getEvent(String eventId);

    Event getEvent(String eventId, boolean getBookingCount);

    List<Event> getBulkEvents(List<String> moduleEventUids, boolean getBookingCount);

    List<Booking> getBookings(String eventId);

}
