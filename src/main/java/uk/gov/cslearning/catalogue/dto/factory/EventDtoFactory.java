package uk.gov.cslearning.catalogue.dto.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.module.DateRange;
import uk.gov.cslearning.catalogue.domain.module.Event;
import uk.gov.cslearning.catalogue.domain.module.FaceToFaceModule;
import uk.gov.cslearning.catalogue.dto.EventDto;
import uk.gov.cslearning.catalogue.service.util.DateRangeComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class EventDtoFactory {
    private final ModuleDtoFactory moduleDtoFactory;
    private final LearningProviderDtoFactory learningProviderDtoFactory;

    public EventDtoFactory(ModuleDtoFactory moduleDtoFactory, LearningProviderDtoFactory learningProviderDtoFactory) {
        this.moduleDtoFactory = moduleDtoFactory;
        this.learningProviderDtoFactory = learningProviderDtoFactory;
    }

    public EventDto create(Event event, FaceToFaceModule module, Course course) {
        System.out.println("***************");
        System.out.println(event);
        System.out.println("***************");
        EventDto eventDto = new EventDto();
        eventDto.setId(event.getId());
        eventDto.setModule(moduleDtoFactory.create(module, course));
        eventDto.setLocation(event.getVenue().getLocation());
        if (event.getDateRanges().size() > 0) {
            eventDto.setEventDate(getEventDatesFromDateRanges(event.getDateRanges()));

        }

        Optional.ofNullable(course.getLearningProvider())
                .ifPresent(learningProvider ->
                        eventDto.setLearningProvider(learningProviderDtoFactory.create(learningProvider)));

        return eventDto;
    }

    public String getEventDatesFromDateRanges(List<DateRange> dateRangesList) {
        List<DateRange> dateRanges = new ArrayList<>(dateRangesList);
        Collections.sort(dateRanges, new DateRangeComparator());
        return dateRanges
                .stream()
                .map(date -> date.getDate().toString())
                .collect(Collectors.joining(","));
    }

}