package uk.gov.cslearning.catalogue.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.module.Event;
import uk.gov.cslearning.catalogue.domain.module.FaceToFaceModule;
import uk.gov.cslearning.catalogue.dto.EventDto;
import uk.gov.cslearning.catalogue.dto.factory.EventDtoFactory;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EventDtoMapService {

    private final EventDateService eventDateService;
    private final EventDtoFactory eventDtoFactory;
    private final long retentionTimeInDays;

    public EventDtoMapService(EventDateService eventDateService, EventDtoFactory eventDtoFactory, @Value("${retention.timeInDays}") long retentionTimeInDays) {
        this.eventDateService = eventDateService;
        this.eventDtoFactory = eventDtoFactory;
        this.retentionTimeInDays = retentionTimeInDays;
    }

    public Map<String, EventDto> getStringEventDtoMap(Iterable<Course> courses) {
        Map<String, EventDto> results = new HashMap<>();

        for (Course course : courses) {
            List<FaceToFaceModule> modules = getFaceToFaceModules(course);

            for (FaceToFaceModule module : modules) {
                for (Event event : module.getEvents()) {
                    results.put(event.getId(), eventDtoFactory.create(event, module, course));
                }
            }
        }
        return results;
    }

    public Map<String, EventDto> getStringEventDtoMapForSupplier(Iterable<Course> courses) {
        Map<String, EventDto> results = new HashMap<>();
        ChronoLocalDate retentionDate = LocalDate.now().minusDays(retentionTimeInDays);

        for (Course course : courses) {
            List<FaceToFaceModule> modules = getFaceToFaceModules(course);

            for (FaceToFaceModule module : modules) {
                for (Event event : module.getEvents()) {
                    eventDateService.getFirstDateChronologically(event).ifPresent(dateRange -> {
                        if (dateRange.getDate().isAfter(retentionDate)) {
                            results.put(event.getId(), eventDtoFactory.create(event, module, course));
                        }
                    });
                }
            }
        }
        return results;
    }

    private List<FaceToFaceModule> getFaceToFaceModules(Course course) {
        return course.getModules().stream()
                .filter(m -> m.getModuleType().equals("face-to-face"))
                .map(m -> (FaceToFaceModule) m)
                .collect(Collectors.toList());
    }
}
