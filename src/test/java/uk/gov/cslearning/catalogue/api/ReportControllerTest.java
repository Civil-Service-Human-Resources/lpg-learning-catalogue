package uk.gov.cslearning.catalogue.api;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.cslearning.catalogue.dto.CourseDto;
import uk.gov.cslearning.catalogue.dto.EventDto;
import uk.gov.cslearning.catalogue.dto.ModuleDto;
import uk.gov.cslearning.catalogue.service.EventService;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
@RunWith(SpringRunner.class)
@WithMockUser(username = "user")
@EnableSpringDataWebSupport
public class ReportControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EventService eventService;

    @Test
    public void shouldReturnMapOfEvents() throws Exception {

        String courseId = "course-id";
        String courseTitle = "course-title";
        CourseDto courseDto = new CourseDto();
        courseDto.setId(courseId);
        courseDto.setTitle(courseTitle);

        String moduleId = "module-id";
        String moduleTitle = "module-title";
        ModuleDto moduleDto = new ModuleDto();
        moduleDto.setId(moduleId);
        moduleDto.setTitle(moduleTitle);
        moduleDto.setCourse(courseDto);

        String eventId = "event_id";
        EventDto event = new EventDto();
        event.setId(eventId);
        event.setModule(moduleDto);

        Map<String, EventDto> events = ImmutableMap.of(eventId, event);

        when(eventService.getEventMap()).thenReturn(events);

        mockMvc.perform(
                get("/reporting/events")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.event_id.id", equalTo(eventId)))
                .andExpect(jsonPath("$.event_id.module.id", equalTo(moduleId)))
                .andExpect(jsonPath("$.event_id.module.title", equalTo(moduleTitle)))
                .andExpect(jsonPath("$.event_id.module.course.id", equalTo(courseId)))
                .andExpect(jsonPath("$.event_id.module.course.title", equalTo(courseTitle)));

    }
}