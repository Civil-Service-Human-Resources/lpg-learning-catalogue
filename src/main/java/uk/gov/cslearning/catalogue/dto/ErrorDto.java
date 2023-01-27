package uk.gov.cslearning.catalogue.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@RequiredArgsConstructor
public class ErrorDto {
    private final Instant timestamp = Instant.now();
    private final List<String> errors;
    private final int status;
    private final String message;

}
