package uk.gov.cslearning.catalogue.service.record.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum BookingStatus {
    REQUESTED("Requested"), CONFIRMED("Confirmed"), CANCELLED("Cancelled");

    private String value;

    BookingStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    public static BookingStatus forValue(String value) {
        return Arrays.stream(BookingStatus.values())
                .filter(v -> v.value.equalsIgnoreCase(value))
                .findAny()
                .orElseThrow(() -> new Error("Error, unknown status: " + value));
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}

