package uk.gov.cslearning.catalogue.dto.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.module.Event;
import uk.gov.cslearning.catalogue.domain.module.FaceToFaceModule;
import uk.gov.cslearning.catalogue.dto.EventDto;

import java.util.Optional;

@Component
public class EventDtoFactory {
    private final ModuleDtoFactory moduleDtoFactory;
    private final CourseDtoFactory courseDtoFactory;
    private final LearningProviderDtoFactory learningProviderDtoFactory;

    public EventDtoFactory(ModuleDtoFactory moduleDtoFactory, CourseDtoFactory courseDtoFactory,
                           LearningProviderDtoFactory learningProviderDtoFactory) {
        this.moduleDtoFactory = moduleDtoFactory;
        this.courseDtoFactory = courseDtoFactory;
        this.learningProviderDtoFactory = learningProviderDtoFactory;
    }

    public EventDto create(Event event, FaceToFaceModule module, Course course) {
        EventDto eventDto = new EventDto();
        eventDto.setId(event.getId());
        eventDto.setModule(moduleDtoFactory.create(module));
        eventDto.setCourse(courseDtoFactory.create(course));

        Optional.ofNullable(course.getLearningProvider())
                .ifPresent(learningProvider ->
                        eventDto.setLearningProvider(learningProviderDtoFactory.create(learningProvider)));

        return eventDto;
    }
}
