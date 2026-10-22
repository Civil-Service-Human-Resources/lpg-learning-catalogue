package uk.gov.cslearning.catalogue.domain.module;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Event {

    private String id = UUID.randomUUID().toString();

    private String joiningInstructions;

    private Venue venue;

    private List<DateRange> dateRanges = new ArrayList<>();

    private EventStatus status;

    private CancellationReason cancellationReason;

}
