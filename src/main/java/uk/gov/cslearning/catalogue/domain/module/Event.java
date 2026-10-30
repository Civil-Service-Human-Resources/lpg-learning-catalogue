package uk.gov.cslearning.catalogue.domain.module;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Event {

    private String id = RandomStringUtils.secure().nextAlphanumeric(22);

    private String joiningInstructions;

    private Venue venue;

    private List<DateRange> dateRanges = new ArrayList<>();

    private EventStatus status;

    private CancellationReason cancellationReason;

}
