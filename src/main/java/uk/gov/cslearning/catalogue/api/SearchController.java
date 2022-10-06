package uk.gov.cslearning.catalogue.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.catalogue.Utils;
import uk.gov.cslearning.catalogue.domain.CivilServant.CivilServant;
import uk.gov.cslearning.catalogue.domain.SearchPage;
import uk.gov.cslearning.catalogue.domain.Status;
import uk.gov.cslearning.catalogue.repository.CourseRepository;
import uk.gov.cslearning.catalogue.service.AuthoritiesService;
import uk.gov.cslearning.catalogue.service.RegistryService;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/search")
public class SearchController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);

    private CourseRepository courseRepository;

    private RegistryService registryService;

    private AuthoritiesService authoritiesService;

    @Autowired
    public SearchController(CourseRepository courseRepository, RegistryService registryService, AuthoritiesService authoritiesService) {
        this.courseRepository = courseRepository;
        this.registryService = registryService;
        this.authoritiesService = authoritiesService;
    }

    @GetMapping("/management/courses")
    public ResponseEntity<SearchResults> searchForOrganisation(
            Authentication authentication,
            @RequestParam(name = "status", defaultValue = "Published") String status,
            @RequestParam(name = "visibility", defaultValue = "PUBLIC") String visibility,
            String query, FilterParameters filterParameters, ProfileParameters profileParameters,
            PageParameters pageParameters) {
        OwnerParameters ownerParameters = new OwnerParameters();
        Pageable pageable = pageParameters.getPageRequest();

        ResponseEntity<SearchResults> response = ResponseEntity.status(HttpStatus.FORBIDDEN).build();

        if (Utils.hasRoles(new String[]{"CSL_AUTHOR", "LEARNING_MANAGER"})) {
            SearchPage searchPage = courseRepository.search(query, pageable, filterParameters, Arrays.stream(status.split(",")).map(Status::forValue).collect(Collectors.toList()), ownerParameters, profileParameters, visibility);
            response = ResponseEntity.ok(new SearchResults(searchPage, pageable));
        } else if (Utils.hasRole("ORGANISATION_AUTHOR")) {
            CivilServant civilServant = registryService.getCurrentCivilServant();
            civilServant.getOrganisationalUnitCode().ifPresent(ownerParameters::setOrganisationalUnitCode);
            SearchPage searchPage = courseRepository.search(query, pageable, filterParameters, Arrays.stream(status.split(",")).map(Status::forValue).collect(Collectors.toList()), ownerParameters, profileParameters, visibility);
            response = ResponseEntity.ok(new SearchResults(searchPage, pageable));
        } else if (Utils.hasRole("PROFESSION_AUTHOR")) {
            CivilServant civilServant = registryService.getCurrentCivilServant();
            civilServant.getProfessionId().ifPresent(organisationalUnitCode -> ownerParameters.setProfession(organisationalUnitCode.toString()));
            SearchPage searchPage = courseRepository.search(query, pageable, filterParameters, Arrays.stream(status.split(",")).map(Status::forValue).collect(Collectors.toList()), ownerParameters, profileParameters, visibility);
            response = ResponseEntity.ok(new SearchResults(searchPage, pageable));
        } else if (Utils.hasRoles(new String[]{"KPMG_SUPPLIER_AUTHOR", "KORNFERRY_SUPPLIER_AUTHOR", "KNOWLEDGEPOOL_SUPPLIER_AUTHOR"})) {
            ownerParameters.setSupplier(authoritiesService.getSupplier(authentication));
            SearchPage searchPage = courseRepository.search(query, pageable, filterParameters, Arrays.stream(status.split(",")).map(Status::forValue).collect(Collectors.toList()), ownerParameters, profileParameters, visibility);
            response = ResponseEntity.ok(new SearchResults(searchPage, pageable));
        }

        return response;
    }
    @GetMapping("/courses")
    public ResponseEntity<SearchResults> search(@RequestParam(name = "status", defaultValue = "Published") String status, @RequestParam(name = "visibility", defaultValue = "PUBLIC") String visibility, String query, FilterParameters filterParameters, ProfileParameters profileParameters, PageParameters pageParameters) {
        OwnerParameters ownerParameters = new OwnerParameters();

        Pageable pageable = pageParameters.getPageRequest();

        SearchPage searchPage = courseRepository.search(query, pageable, filterParameters, Arrays.stream(status.split(",")).map(Status::forValue).collect(Collectors.toList()), ownerParameters, profileParameters, visibility);

        return ResponseEntity.ok(new SearchResults(searchPage, pageable));
    }
}
