package uk.gov.cslearning.catalogue.domain.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.elasticsearch.common.UUIDs;

import java.time.LocalDateTime;

public class Event {

    private String id = UUIDs.randomBase64UUID();

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)

    private LocalDateTime date;
    private String location;
    private Integer capacity;

    @JsonCreator
    public Event(@JsonProperty("date") LocalDateTime date,
                 @JsonProperty("location") String location,
                 @JsonProperty("capacity") Integer capacity) {
        this.date = date;
        this.location = location;
        this.capacity = capacity;
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
    public Integer getCapacity() {
        return capacity;
    }

}
