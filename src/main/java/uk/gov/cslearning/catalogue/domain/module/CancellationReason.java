package uk.gov.cslearning.catalogue.domain.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum CancellationReason {
    UNAVAILABLE("the event is no longer available"),VENUE("short notice unavailability of the venue");

    private final String value;

    CancellationReason(String value) {
        this.value = value;
    }

    @JsonCreator
    public static CancellationReason forValue(String value) {
        return Arrays.stream(CancellationReason.values())
        .filter(v -> v.value.equalsIgnoreCase(value))
        .findAny()
        .orElseThrow(() -> new Error("Error, unknown status: " + value));
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}


