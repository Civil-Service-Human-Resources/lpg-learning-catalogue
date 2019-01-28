package uk.gov.cslearning.catalogue.domain.module;

import org.elasticsearch.common.UUIDs;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class Event {

    private String id = UUIDs.randomBase64UUID();

    private String joiningInstructions;

    private Venue venue;

    private List<DateRange> dateRanges = new ArrayList<>();

    private EventStatus status;

    private CancellationReason cancellationReason;

    public Event() {
    }

    public String getJoiningInstructions() {
        return joiningInstructions;
    }

    public void setJoiningInstructions(String joiningInstructions) {
        this.joiningInstructions = joiningInstructions;
    }

    public List<DateRange> getDateRanges() {
        return unmodifiableList(dateRanges);
    }

    public void setDateRanges(List<DateRange> dateRanges) {
        this.dateRanges.clear();
        if (dateRanges != null) {
            this.dateRanges.addAll(dateRanges);
        }
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public CancellationReason getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(CancellationReason cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
}
