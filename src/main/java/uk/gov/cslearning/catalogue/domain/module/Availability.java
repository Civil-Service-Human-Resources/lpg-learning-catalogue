package uk.gov.cslearning.catalogue.domain.module;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.time.LocalDateTime;

@Document(indexName = "lpg", type="availability")
public class Availability {

    @Id
    private String id;

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
