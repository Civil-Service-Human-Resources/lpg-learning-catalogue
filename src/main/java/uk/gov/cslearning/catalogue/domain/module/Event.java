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

    public String getId() {
        return id;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }


}
