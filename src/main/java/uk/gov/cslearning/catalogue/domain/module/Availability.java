package uk.gov.cslearning.catalogue.domain.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.elasticsearch.common.UUIDs;

import java.time.LocalDateTime;

public class Availability {

    private String id = UUIDs.randomBase64UUID();

    private LocalDateTime date;

    private String location;

    @JsonCreator
    public Availability(@JsonProperty("date") LocalDateTime date,
                        @JsonProperty("location") String location) {
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
