package uk.gov.cslearning.catalogue.domain.module;

import org.elasticsearch.common.UUIDs;

import java.time.LocalDateTime;

public class Availability {

    private String id = UUIDs.randomBase64UUID();

    private LocalDateTime date;

    private String location;

    public Availability(LocalDateTime date, String location) {
        this.date = date;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }
}
