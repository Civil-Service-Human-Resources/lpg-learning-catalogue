package uk.gov.cslearning.catalogue.service;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.domain.module.DateRange;
import uk.gov.cslearning.catalogue.domain.module.Event;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventDateService {

    public Optional<DateRange> getFirstDateChronologically(Event event) {
        List<DateRange> sortedList = event.getDateRanges()
                .stream()
                .sorted(Comparator.comparing(DateRange::getDate))
                .collect(Collectors.toList());

        return Optional.of(sortedList.get(0));
    }
}
