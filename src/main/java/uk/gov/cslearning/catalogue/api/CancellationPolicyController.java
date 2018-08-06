package uk.gov.cslearning.catalogue.api;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.catalogue.domain.CancellationPolicy;
import uk.gov.cslearning.catalogue.repository.CancellationPolicyRepository;

import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/cancellation-policy")
public class CancellationPolicyController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CancellationPolicyController.class);

    private final CancellationPolicyRepository cancellationPolicyRepository;

    @Autowired
    public CancellationPolicyController(CancellationPolicyRepository cancellationPolicyRepository) {
        this.cancellationPolicyRepository = cancellationPolicyRepository;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody CancellationPolicy cancellationPolicy, UriComponentsBuilder builder) {
        LOGGER.debug("Creating CancellationPolicy {}", cancellationPolicy.toString());

        CancellationPolicy newCancellationPolicy = cancellationPolicyRepository.save(cancellationPolicy);

        return ResponseEntity.created(builder.path("/cancellation-policy/{cancellationPolicyId}").build(newCancellationPolicy.getId())).build();
    }

    @GetMapping("/{cancellationPolicyId}")
    public ResponseEntity<CancellationPolicy> get(@PathVariable("cancellationPolicyId") String cancellationPolicyId) {
        LOGGER.debug("Getting cancellation policy with ID {}", cancellationPolicyId);

        Optional<CancellationPolicy> result = cancellationPolicyRepository.findById(cancellationPolicyId);

        return result
                .map(course -> new ResponseEntity<>(course, OK))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }

    @PutMapping(path = "/{cancellationPolicyId}")
    public ResponseEntity<Void> update(@PathVariable("cancellationPolicyId") String cancellationPolicyId, @RequestBody CancellationPolicy cancellationPolicy) {
        LOGGER.debug("Updating cancellation policy {}", cancellationPolicy.toString());
        if (!cancellationPolicyId.equals(cancellationPolicy.getId())) {
            return ResponseEntity.badRequest().build();
        }
        if (!cancellationPolicyRepository.existsById(cancellationPolicyId)) {
            return ResponseEntity.badRequest().build();
        }
        cancellationPolicyRepository.save(cancellationPolicy);

        return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/{cancellationPolicyId}")
    public ResponseEntity<Void> delete(@PathVariable("cancellationPolicyId") String cancellationPolicyId, @RequestBody CancellationPolicy cancellationPolicy) {
        LOGGER.debug("Deleting cancellation policy {}", cancellationPolicy.toString());
        if (!cancellationPolicyId.equals(cancellationPolicy.getId())) {
            return ResponseEntity.badRequest().build();
        }
        if (!cancellationPolicyRepository.existsById(cancellationPolicyId)) {
            return ResponseEntity.badRequest().build();
        }
        cancellationPolicyRepository.delete(cancellationPolicy);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/list")
    public ResponseEntity<PageResults<CancellationPolicy>> list(
            PageParameters pageParameters) {
        LOGGER.debug("Listing cancellation policies with {}", pageParameters.toString());

        Pageable pageable = pageParameters.getPageRequest();
        Page<CancellationPolicy> page;

        page = cancellationPolicyRepository.findAll(pageable);

        return ResponseEntity.ok(new PageResults<>(page, pageable));
    }
}
