package uk.gov.cslearning.catalogue.dto.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.module.DateRange;
import uk.gov.cslearning.catalogue.domain.module.Event;
import uk.gov.cslearning.catalogue.domain.module.FaceToFaceModule;
import uk.gov.cslearning.catalogue.dto.EventDto;

import java.util.List;
import java.util.Optional;

@Component
public class EventDtoFactory {
    private final ModuleDtoFactory moduleDtoFactory;
    private final LearningProviderDtoFactory learningProviderDtoFactory;

    public EventDtoFactory(ModuleDtoFactory moduleDtoFactory, LearningProviderDtoFactory learningProviderDtoFactory) {
        this.moduleDtoFactory = moduleDtoFactory;
        this.learningProviderDtoFactory = learningProviderDtoFactory;
    }

    public EventDto create(Event event, FaceToFaceModule module, Course course) {
        EventDto eventDto = new EventDto();
        eventDto.setId(event.getId());
        eventDto.setModule(moduleDtoFactory.create(module, course));
        eventDto.setLocation(event.getVenue().getLocation());
        eventDto.setEventDate(getEventDatesFromDateRanges(event.getDateRanges()));

        Optional.ofNullable(course.getLearningProvider())
                .ifPresent(learningProvider ->
                        eventDto.setLearningProvider(learningProviderDtoFactory.create(learningProvider)));

        return eventDto;
    }

    public String getEventDatesFromDateRanges(List<DateRange> dateRangesList) {
        String eventDate = "";
        for (int i = 0; i < dateRangesList.size(); i++) {
            eventDate += dateRangesList.get(i).getDate().toString();
            if (i != dateRangesList.size() - 1) {
                eventDate += ", ";
            }
        }

        return eventDate;
    }
}