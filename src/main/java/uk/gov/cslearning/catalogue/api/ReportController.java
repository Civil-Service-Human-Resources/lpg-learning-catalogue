package uk.gov.cslearning.catalogue.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.catalogue.dto.EventDto;
import uk.gov.cslearning.catalogue.service.EventService;

import java.util.Map;

@RestController
@RequestMapping("/reporting")
public class ReportController {

    private final EventService eventService;

    public ReportController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public ResponseEntity<Map<String, EventDto>> getEvents() {
        return ResponseEntity.ok(eventService.getEventMap());
    }
}
