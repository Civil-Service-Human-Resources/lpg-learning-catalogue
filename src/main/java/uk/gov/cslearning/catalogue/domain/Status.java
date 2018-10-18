package uk.gov.cslearning.catalogue.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum Status {
    DRAFT("Draft"),
    PUBLISHED("Published"),
    ARCHIVED("Archived");

    private final String value;

    Status(String value) {
        this.value = value;
    }

    @JsonCreator
    public static Status forValue(String value) {
        return Arrays.stream(Status.values())
                .filter(v -> v.value.equalsIgnoreCase(value))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Uknown Status"));
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
