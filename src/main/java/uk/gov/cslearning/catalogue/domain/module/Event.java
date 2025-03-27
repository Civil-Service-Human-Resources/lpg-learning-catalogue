package uk.gov.cslearning.catalogue.domain.module;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.elasticsearch.common.UUIDs;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Event {

    private String id = UUIDs.randomBase64UUID();

    private String joiningInstructions;

    private Venue venue;

    private List<DateRange> dateRanges = new ArrayList<>();

    private EventStatus status;

    private CancellationReason cancellationReason;

}
