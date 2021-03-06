package uk.gov.cslearning.catalogue.dto.factory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.LearningProvider;
import uk.gov.cslearning.catalogue.domain.module.DateRange;
import uk.gov.cslearning.catalogue.domain.module.Event;
import uk.gov.cslearning.catalogue.domain.module.FaceToFaceModule;
import uk.gov.cslearning.catalogue.domain.module.Venue;
import uk.gov.cslearning.catalogue.dto.CourseDto;
import uk.gov.cslearning.catalogue.dto.EventDto;
import uk.gov.cslearning.catalogue.dto.LearningProviderDto;
import uk.gov.cslearning.catalogue.dto.ModuleDto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
        Venue venue = new Venue();
        List<DateRange> dateRanges = new ArrayList<>();

        ModuleDto moduleDto = new ModuleDto();
        LearningProviderDto learningProviderDto = new LearningProviderDto();

        course.setLearningProvider(learningProvider);
        event.setDateRanges(dateRanges);
        event.setVenue(venue);

        when(moduleDtoFactory.create(module, course)).thenReturn(moduleDto);
        when(learningProviderDtoFactory.create(learningProvider)).thenReturn(learningProviderDto);

        EventDto eventDto = eventDtoFactory.create(event, module, course);

        assertEquals(moduleDto, eventDto.getModule());
        assertEquals(learningProviderDto, eventDto.getLearningProvider());
        assertNotNull(eventDto.getId());
        assertEquals(event.getId(), eventDto.getId());
        assertEquals(event.getVenue().getLocation(), eventDto.getLocation());
        if (event.getDateRanges().size() > 0) {
            assertEquals(eventDtoFactory.getEventDatesFromDateRanges(event.getDateRanges()), eventDto.getEventDate());
        }
    }

    @Test
    public void shouldReturnEventDtoWithoutLearningProvider() {
        Event event = new Event();
        FaceToFaceModule module = new FaceToFaceModule("product-code");
        Course course = new Course();
        Venue venue = new Venue();
        List<DateRange> dateRanges = populateEventDates();

        event.setDateRanges(dateRanges);
        event.setVenue(venue);

        ModuleDto moduleDto = new ModuleDto();

        when(moduleDtoFactory.create(module, course)).thenReturn(moduleDto);

        EventDto eventDto = eventDtoFactory.create(event, module, course);


        assertEquals(moduleDto, eventDto.getModule());
        assertNull(eventDto.getLearningProvider());
        assertNotNull(eventDto.getId());
        assertEquals(event.getId(), eventDto.getId());
        assertEquals(event.getVenue().getLocation(), eventDto.getLocation());
        assertEquals(eventDtoFactory.getEventDatesFromDateRanges(event.getDateRanges()), eventDto.getEventDate());

        verifyZeroInteractions(learningProviderDtoFactory);
    }

    private List<DateRange> populateEventDates() {
        LocalDate date = LocalDate.now();
        LocalTime start = LocalTime.NOON;
        LocalTime end = LocalTime.MIDNIGHT;

        DateRange dateRange1 = new DateRange();
        dateRange1.setDate(date);
        dateRange1.setStartTime(start);
        dateRange1.setEndTime(end);

        DateRange dateRange2 = new DateRange();
        dateRange2.setDate(date.plusDays(5));
        dateRange2.setStartTime(start);
        dateRange2.setEndTime(end);

        List<DateRange> dates = new ArrayList<>();
        dates.add(dateRange1);
        dates.add(dateRange2);

        return dates;
    }

}
