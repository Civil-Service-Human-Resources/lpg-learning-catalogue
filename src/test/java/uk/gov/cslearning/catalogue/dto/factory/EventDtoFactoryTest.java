package uk.gov.cslearning.catalogue.dto.factory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.LearningProvider;
import uk.gov.cslearning.catalogue.domain.module.Event;
import uk.gov.cslearning.catalogue.domain.module.FaceToFaceModule;
import uk.gov.cslearning.catalogue.dto.CourseDto;
import uk.gov.cslearning.catalogue.dto.EventDto;
import uk.gov.cslearning.catalogue.dto.LearningProviderDto;
import uk.gov.cslearning.catalogue.dto.ModuleDto;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EventDtoFactoryTest {

    @Mock
    private ModuleDtoFactory moduleDtoFactory;

    @Mock
    private LearningProviderDtoFactory learningProviderDtoFactory;

    @InjectMocks
    private EventDtoFactory eventDtoFactory;

    @Test
    public void shouldReturnEventDtoWithLearningProvider() {
        Event event = new Event();
        FaceToFaceModule module = new FaceToFaceModule("product-code");
        Course course = new Course();
        LearningProvider learningProvider = new LearningProvider();

        ModuleDto moduleDto = new ModuleDto();
        CourseDto courseDto = new CourseDto();
        LearningProviderDto learningProviderDto = new LearningProviderDto();

        course.setLearningProvider(learningProvider);

        when(moduleDtoFactory.create(module, course)).thenReturn(moduleDto);
        when(learningProviderDtoFactory.create(learningProvider)).thenReturn(learningProviderDto);

        EventDto eventDto = eventDtoFactory.create(event, module, course);

        assertEquals(moduleDto, eventDto.getModule());
        assertEquals(learningProviderDto, eventDto.getLearningProvider());
        assertNotNull(eventDto.getId());
        assertEquals(event.getId(), eventDto.getId());
    }

    @Test
    public void shouldReturnEventDtoWithoutLearningProvider() {
        Event event = new Event();
        FaceToFaceModule module = new FaceToFaceModule("product-code");
        Course course = new Course();

        ModuleDto moduleDto = new ModuleDto();

        when(moduleDtoFactory.create(module,course)).thenReturn(moduleDto);

        EventDto eventDto = eventDtoFactory.create(event, module, course);

        assertEquals(moduleDto, eventDto.getModule());
        assertNull(eventDto.getLearningProvider());
        assertNotNull(eventDto.getId());
        assertEquals(event.getId(), eventDto.getId());

        verifyZeroInteractions(learningProviderDtoFactory);
    }

}
