package uk.gov.cslearning.catalogue.api;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.catalogue.dto.EventDto;
import uk.gov.cslearning.catalogue.dto.ModuleDto;
import uk.gov.cslearning.catalogue.mapping.RoleMapping;
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

    @GetMapping("/modules")
    public ResponseEntity<Map<String, ModuleDto>> getModules() {
        return ResponseEntity.ok(moduleService.getModuleMap());
    }

    @RoleMapping("KPMG_SUPPLIER_REPORTER")
    @GetMapping("/modules")
    public ResponseEntity<Map<String, ModuleDto>> getModulesForKPMG(Pageable pageable) {
        return ResponseEntity.ok(moduleService.getModuleMapForSupplier("KPMG"));
    }

    @RoleMapping("KORNFERRY_SUPPLIER_REPORTER")
    @GetMapping("/modules")
    public ResponseEntity<Map<String, ModuleDto>> getModulesForKornferry(Pageable pageable) {
        return ResponseEntity.ok(moduleService.getModuleMapForSupplier("KORNFERRY"));
    }

    @RoleMapping("KPMG_SUPPLIER_REPORTER")
    @GetMapping("/events")
    public ResponseEntity<Map<String, EventDto>> getEventsForSupplier(Pageable pageable) {
        return ResponseEntity.ok(eventService.getEventMapBySupplier("KPMG"));
    }

    @RoleMapping("KORNFERRY_SUPPLIER_REPORTER")
    @GetMapping("/events")
    public ResponseEntity<Map<String, EventDto>> getEventsForKornferrySupplier(Pageable pageable) {
        return ResponseEntity.ok(eventService.getEventMapBySupplier("KORNFERRY"));
    }

    @GetMapping("/events")
    public ResponseEntity<Map<String, EventDto>> getEvents() {
        return ResponseEntity.ok(eventService.getEventMap());
    }
}
