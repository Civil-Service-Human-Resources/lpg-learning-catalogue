package uk.gov.cslearning.catalogue.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.domain.module.DateRange;
import uk.gov.cslearning.catalogue.domain.module.Event;

import java.util.Comparator;
import java.util.Optional;

@Service
public class EventDateService {

    public Optional<DateRange> getFirstDateChronologically(Event event) {
        return event.getDateRanges()
                .stream()
                .min(Comparator.comparing(DateRange::getDate));
    }
}
