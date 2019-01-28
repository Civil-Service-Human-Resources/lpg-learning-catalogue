package uk.gov.cslearning.catalogue.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.catalogue.dto.EventDto;
import uk.gov.cslearning.catalogue.dto.ModuleDto;
import uk.gov.cslearning.catalogue.service.EventService;
import uk.gov.cslearning.catalogue.service.ModuleService;

import java.util.Map;

@RestController
@RequestMapping("/reporting")
public class ReportController {

    private final EventService eventService;
    private final ModuleService moduleService;

    public ReportController(EventService eventService, ModuleService moduleService) {
        this.eventService = eventService;
        this.moduleService = moduleService;
    }

    @GetMapping("/events")
    public ResponseEntity<Map<String, EventDto>> getEvents() {
        return ResponseEntity.ok(eventService.getEventMap());
    }

    @GetMapping("/modules")
    public ResponseEntity<Map<String, ModuleDto>> getModules() {
        return ResponseEntity.ok(moduleService.getModuleMap());
    }

    @GetMapping(value = "/modules", params = "professionId")
    public ResponseEntity<Map<String, ModuleDto>> getModules(@RequestParam String professionId) {
        return ResponseEntity.ok(moduleService.getModuleMap(professionId));
    }
}
