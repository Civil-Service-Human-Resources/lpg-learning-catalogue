package uk.gov.cslearning.catalogue.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import uk.gov.cslearning.catalogue.exception.UnknownStatusException;

import java.net.URL;
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

    @WritingConverter
    public class StatusToStringConverter implements Converter<Status, String> {

        @Override
        public String convert(Status source) {
            return source.getValue();
        }
    }

    @ReadingConverter
    public class StringtoStatusConverter implements Converter<String, Status>  {

        @SneakyThrows
        @Override
        public Status convert(@NotNull String source) {
            return Status.forValue(source);
        }
    }
}
