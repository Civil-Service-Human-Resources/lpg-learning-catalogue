package uk.gov.cslearning.catalogue.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import uk.gov.cslearning.catalogue.exception.UnknownStatusException;

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
                .orElseThrow(() -> new UnknownStatusException(value));
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
