package uk.gov.cslearning.catalogue.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.List;

@Data
@RequiredArgsConstructor
public class ErrorDto {
    private final Instant timestamp = Instant.now();
    private List<String> errors;
    private int status;
    private String message;

    @JsonIgnore
    public ResponseEntity<Object> getAsResponseEntity() {
        return new ResponseEntity<>(this, HttpStatus.valueOf(getStatus()));
    }
}