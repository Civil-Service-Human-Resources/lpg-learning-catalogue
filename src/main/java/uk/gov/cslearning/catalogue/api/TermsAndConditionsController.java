package uk.gov.cslearning.catalogue.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.catalogue.domain.TermsAndConditions;
import uk.gov.cslearning.catalogue.repository.TermsAndConditionsRepository;

import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

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
        LOGGER.debug("Creating Terms and Conditions {}", termsAndConditions.toString());

        TermsAndConditions newTermsAndConditions = termsAndConditionsRepository.save(termsAndConditions);

        return ResponseEntity.created(builder.path("/terms-and-conditions/{termsAndConditionsId}").build(newTermsAndConditions.getId())).build();
    }

    @GetMapping("/{termsAndConditionsId}")
    public ResponseEntity<TermsAndConditions> get(@PathVariable("termsAndConditionsId") String termsAndConditionsId) {
        LOGGER.debug("Getting Terms and Conditions with ID {}", termsAndConditionsId);

        Optional<TermsAndConditions> result = termsAndConditionsRepository.findById(termsAndConditionsId);

        return result
                .map(course -> new ResponseEntity<>(course, OK))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }

    @PutMapping(path = "/{termsAndConditionsId}")
    public ResponseEntity<Void> update(@PathVariable("termsAndConditionsId") String termsAndConditionsId, @RequestBody TermsAndConditions termsAndConditions) {
        LOGGER.debug("Updating Terms and Conditions {}", termsAndConditions.toString());

        if (!termsAndConditionsId.equals(termsAndConditions.getId())) {
            return ResponseEntity.badRequest().build();
        }
        if (!termsAndConditionsRepository.existsById(termsAndConditionsId)) {
            return ResponseEntity.badRequest().build();
        }
        termsAndConditionsRepository.save(termsAndConditions);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/{termsAndConditionsId}")
    public ResponseEntity<Void> delete(@PathVariable("termsAndConditionsId") String termsAndConditionsId, @RequestBody TermsAndConditions termsAndConditions) {
        LOGGER.debug("Deleting Terms and Conditions{}", termsAndConditions.toString());

        if (!termsAndConditionsId.equals(termsAndConditions.getId())) {
            return ResponseEntity.badRequest().build();
        }
        if (!termsAndConditionsRepository.existsById(termsAndConditionsId)) {
            return ResponseEntity.badRequest().build();
        }
        termsAndConditionsRepository.delete(termsAndConditions);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/list")
    public ResponseEntity<PageResults<TermsAndConditions>> list(
            PageParameters pageParameters) {
        LOGGER.debug("Listing Terms and Conditions with {}", pageParameters.toString());

        Pageable pageable = pageParameters.getPageRequest();
        Page<TermsAndConditions> page;

        page = termsAndConditionsRepository.findAll(pageable);

        return ResponseEntity.ok(new PageResults<>(page, pageable));
    }
}
