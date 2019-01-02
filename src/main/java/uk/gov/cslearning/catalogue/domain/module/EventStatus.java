package uk.gov.cslearning.catalogue.domain.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum EventStatus {
    ACTIVE("Active"),CANCELLED("Cancelled");

    private final String value;

    EventStatus(String value) {
        this.value = value;
    }

    @JsonCreator
    public static EventStatus forValue(String value) {
        return Arrays.stream(EventStatus.values())
                .filter(v -> v.value.equalsIgnoreCase(value))
                .findAny()
                .orElseThrow(() -> new Error("Error, unknown status: " + value));
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}

