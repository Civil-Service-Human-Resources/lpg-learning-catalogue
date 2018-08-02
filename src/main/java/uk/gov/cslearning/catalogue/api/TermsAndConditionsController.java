package uk.gov.cslearning.catalogue.api;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.catalogue.domain.TermsAndConditions;
import uk.gov.cslearning.catalogue.repository.TermsAndConditionsRepository;

@RestController
@RequestMapping("/terms-and-conditions")
public class TermsAndConditionsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TermsAndConditionsController.class);
    private final TermsAndConditionsRepository termsAndConditionsRepository;

    @Autowired
    public TermsAndConditionsController(TermsAndConditionsRepository termsAndConditionsRepository) {
        this.termsAndConditionsRepository = termsAndConditionsRepository;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody TermsAndConditions termsAndConditions, UriComponentsBuilder builder) {
        LOGGER.debug("Creating TermsAndConditions {}", termsAndConditions);

        TermsAndConditions newTermsAndConditions = termsAndConditionsRepository.save(termsAndConditions);

        return ResponseEntity.created(builder.path("/terms-and-conditions/{termsAndConditionsId}").build(newTermsAndConditions.getId())).build();
    }
}
