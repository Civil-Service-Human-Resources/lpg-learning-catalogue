package uk.gov.cslearning.catalogue.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.catalogue.dto.EventDto;
import uk.gov.cslearning.catalogue.dto.ModuleDto;
import uk.gov.cslearning.catalogue.mapping.RoleMapping;
import uk.gov.cslearning.catalogue.service.EventService;
import uk.gov.cslearning.catalogue.service.LearningProviderService;
import uk.gov.cslearning.catalogue.service.ModuleService;

import java.util.Map;

@RestController
@RequestMapping("/reporting")
public class ReportController {

    private final EventService eventService;
    private final ModuleService moduleService;
    private final LearningProviderService learningProviderService;

    public ReportController(EventService eventService, ModuleService moduleService, LearningProviderService learningProviderService) {
        this.eventService = eventService;
        this.moduleService = moduleService;
        this.learningProviderService = learningProviderService;
    }

    @GetMapping("/events")
    public ResponseEntity<Map<String, EventDto>> getEvents() {
        return ResponseEntity.ok(eventService.getEventMap());
    }

    @RoleMapping({"KPMG_SUPPLIER_REPORTER", "KORNFERRY_SUPPLIER_REPORTER", "KNOWLEDGEPOOL_SUPPLIER_REPORTER"})
    @GetMapping("/modules")
    public ResponseEntity<Map<String, ModuleDto>> getModulesForSupplier(Authentication authentication) {
        String learningProviderName = learningProviderService.getLearningProviderNameFromAuthentication(authentication);
        return ResponseEntity.ok(moduleService.getModuleMapForSupplier(learningProviderName));
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
