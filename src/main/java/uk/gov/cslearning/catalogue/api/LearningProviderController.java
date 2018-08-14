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
import uk.gov.cslearning.catalogue.domain.LearningProvider;
import uk.gov.cslearning.catalogue.domain.TermsAndConditions;
import uk.gov.cslearning.catalogue.repository.LearningProviderRepository;

import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/learning-providers")
public class LearningProviderController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LearningProviderController.class);

    private final LearningProviderRepository learningProviderRepository;

    @Autowired
    public LearningProviderController(LearningProviderRepository learningProviderRepository) {
        this.learningProviderRepository = learningProviderRepository;
    }

    @GetMapping
    public ResponseEntity<PageResults<LearningProvider>> list(
            PageParameters pageParameters) {
        LOGGER.debug("Listing Learning Providers with {}", pageParameters.toString());

        Pageable pageable = pageParameters.getPageRequest();
        Page<LearningProvider> page;

        page = learningProviderRepository.findAll(pageable);

        return ResponseEntity.ok(new PageResults<>(page, pageable));
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody LearningProvider learningProvider, UriComponentsBuilder builder) {
        LOGGER.debug("Creating Learning Provider {}", learningProvider.toString());

        LearningProvider newLearningProvider = learningProviderRepository.save(learningProvider);

        return ResponseEntity.created(builder.path("/learning-providers/{learningProviderId}").build(newLearningProvider.getId())).build();
    }

    @GetMapping("/{learningProviderId}")
    public ResponseEntity<LearningProvider> get(@PathVariable("learningProviderId") String learningProviderId) {
        LOGGER.info("Getting Learning Provider with Id {}", learningProviderId);

        Optional<LearningProvider> result = learningProviderRepository.findById(learningProviderId);

        return result
                .map(course -> new ResponseEntity<>(course, OK))
                .orElseGet(() -> new ResponseEntity<>(NOT_FOUND));
    }

    @PutMapping(path = "/{learningProviderId}")
    public ResponseEntity<Void> update(@PathVariable("learningProviderId") String learningProviderId, @RequestBody LearningProvider learningProvider) {
        LOGGER.debug("Updating Learning Provider {}", learningProvider.toString());

        if (!learningProviderId.equals(learningProvider.getId())) {
            return ResponseEntity.badRequest().build();
        }
        if (!learningProviderRepository.existsById(learningProviderId)) {
            return ResponseEntity.badRequest().build();
        }
        learningProviderRepository.save(learningProvider);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{learningProviderId}")
    public ResponseEntity<Void> delete(@PathVariable("learningProviderId") String learningProviderId) {
        LOGGER.debug("Deleting Learning Provider by Id {}", learningProviderId);

        if (!learningProviderRepository.existsById(learningProviderId)) {
            return ResponseEntity.badRequest().build();
        }
        learningProviderRepository.deleteById(learningProviderId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping(path="/{learningProviderId}/cancellation-policies/{cancellationPolicyId}")
    public ResponseEntity getCancellationPolicy(@PathVariable("learningProviderId") String learningProviderId, @PathVariable
            ("cancellationPolicyId") String cancellationPolicyId){
        LOGGER.info("Getting Cancellation Policy with Id {}", cancellationPolicyId);

        if (!learningProviderRepository.existsById(learningProviderId)) {
            return ResponseEntity.badRequest().build();
        }

        Optional<LearningProvider> result = learningProviderRepository.findById(learningProviderId);

        return result.map(learningProvider -> {
            CancellationPolicy cancellationPolicy = learningProvider.getCancellationPolicyById(cancellationPolicyId);

            return ResponseEntity.ok().body(cancellationPolicy);
        }).orElse(ResponseEntity.noContent().build());
    }

    @PostMapping(path = "/{learningProviderId}/cancellation-policies")
    public ResponseEntity<Object> addCancellationPolicyToLearningProvider(@PathVariable("learningProviderId") String learningProviderId, @RequestBody
            CancellationPolicy cancellationPolicy, UriComponentsBuilder builder) {
        LOGGER.debug("Adding Cancellation Policy {} to Learning Provider with Id {}", cancellationPolicy.getName(), learningProviderId);

        if (!learningProviderRepository.existsById(learningProviderId)) {
            return ResponseEntity.badRequest().build();
        }

        Optional<LearningProvider> result = learningProviderRepository.findById(learningProviderId);

        return result.map(learningProvider -> {
            learningProvider.addCancellationPolicy(cancellationPolicy);

            learningProviderRepository.save(learningProvider);

            return ResponseEntity.created(builder.path("/learning-providers/{learningProviderId}").build(learningProvider.getId())).build();

        }).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PutMapping(path = "/{learningProviderId}/cancellation-policies/{cancellationPolicyId}")
    public ResponseEntity<Object> updateCancellationPolicyInLearningProvider(@PathVariable("learningProviderId") String learningProviderId, @PathVariable("cancellationPolicyId")
            String cancellationPolicyId, @RequestBody CancellationPolicy newCancellationPolicy){
        LOGGER.debug("Updating Cancellation Policy with Id in Learning Provider with Id {}", learningProviderId);

        if(!learningProviderRepository.existsById(learningProviderId)){
            return ResponseEntity.badRequest().build();
        }

        Optional<LearningProvider> result = learningProviderRepository.findById(learningProviderId);

        return result.map(learningProvider -> {
            CancellationPolicy cancellationPolicy = learningProvider.getCancellationPolicyById(cancellationPolicyId);

            cancellationPolicy.setName(newCancellationPolicy.getName());
            cancellationPolicy.setFullVersion(newCancellationPolicy.getFullVersion());
            cancellationPolicy.setShortVersion(newCancellationPolicy.getShortVersion());

            learningProviderRepository.save(learningProvider);

            return ResponseEntity.noContent().build();
        }).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping(path = "/{learningProviderId}/cancellation-policies/{cancellationPolicyId}")
    public ResponseEntity<Object> deleteCancellationPolicyInLearningProvider(@PathVariable("learningProviderId") String learningProviderId, @PathVariable("cancellationPolicyId") String cancellationPolicyId){
        LOGGER.debug("Updating Cancellation Policy with Id in Learning Provider with Id {}", learningProviderId);

        if(!learningProviderRepository.existsById(learningProviderId)){
            return ResponseEntity.badRequest().build();
        }

        Optional<LearningProvider> result = learningProviderRepository.findById(learningProviderId);

        return result.map(learningProvider -> {
            CancellationPolicy cancellationPolicy = learningProvider.getCancellationPolicyById(cancellationPolicyId);

            learningProvider.removeCancellationPolicy(cancellationPolicy);

            learningProviderRepository.save(learningProvider);

            return ResponseEntity.noContent().build();
        }).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @GetMapping(path="/{learningProviderId}/terms-and-conditions/{termsAndConditionsId}")
    public ResponseEntity getTermsAndConditions(@PathVariable("learningProviderId") String learningProviderId, @PathVariable
            ("termsAndConditionsId") String termsAndConditionsId){
        LOGGER.info("Getting Terms and Conditions with Id {}", termsAndConditionsId);

        if (!learningProviderRepository.existsById(learningProviderId)) {
            return ResponseEntity.badRequest().build();
        }

        Optional<LearningProvider> result = learningProviderRepository.findById(learningProviderId);

        return result.map(learningProvider -> {
            TermsAndConditions termsAndConditions = learningProvider.getTermsAndConditionsById(termsAndConditionsId);

            return ResponseEntity.ok().body(termsAndConditions);
        }).orElse(ResponseEntity.noContent().build());
    }

    @PostMapping(path = "/{learningProviderId}/terms-and-conditions")
    public ResponseEntity<Object> addTermsAndConditionsToLearningProvider(@PathVariable("learningProviderId") String learningProviderId, @RequestBody
            TermsAndConditions termsAndConditions, UriComponentsBuilder builder) {
        LOGGER.debug("Adding Terms And Conditions {} to Learning Provider with Id {}", termsAndConditions.getName(), learningProviderId);

        if (!learningProviderRepository.existsById(learningProviderId)) {
            return ResponseEntity.badRequest().build();
        }

        Optional<LearningProvider> result = learningProviderRepository.findById(learningProviderId);

        return result.map(learningProvider -> {
            learningProvider.addTermsAndConditions(termsAndConditions);

            learningProviderRepository.save(learningProvider);

            return ResponseEntity.created(builder.path("/learning-providers/{learningProviderId}").build(learningProvider.getId())).build();

        }).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @PutMapping(path = "/{learningProviderId}/terms-and-conditions/{termsAndConditionsId}")
    public ResponseEntity<Object> updateTermsAndConditionsInLearningProvider(@PathVariable("learningProviderId") String learningProviderId, @PathVariable("termsAndConditionsId")
            String termsAndConditionsId, @RequestBody TermsAndConditions newTermsAndConditions){
        LOGGER.debug("Updating Learning Provider with Id {}", learningProviderId);

        if(!learningProviderRepository.existsById(learningProviderId)){
            return ResponseEntity.badRequest().build();
        }

        Optional<LearningProvider> result = learningProviderRepository.findById(learningProviderId);

        return result.map(learningProvider -> {
            TermsAndConditions termsAndConditions = learningProvider.getTermsAndConditionsById(termsAndConditionsId);

            termsAndConditions.setName(newTermsAndConditions.getName());
            termsAndConditions.setContent(newTermsAndConditions.getContent());

            learningProviderRepository.save(learningProvider);

            return ResponseEntity.noContent().build();
        }).orElseGet(() -> ResponseEntity.badRequest().build());
    }

    @DeleteMapping(path = "/{learningProviderId}/terms-and-conditions/{termsAndConditionsId}")
    public ResponseEntity<Object> deleteTermsAndConditionsInLearningProvider(@PathVariable("learningProviderId") String learningProviderId, @PathVariable("termsAndConditionsId")
            String termsAndConditionsId){
        LOGGER.debug("Updating Learning Provider with Id {}", learningProviderId);

        if(!learningProviderRepository.existsById(learningProviderId)){
            return ResponseEntity.badRequest().build();
        }

        Optional<LearningProvider> result = learningProviderRepository.findById(learningProviderId);

        return result.map(learningProvider -> {
            TermsAndConditions termsAndConditions = learningProvider.getTermsAndConditionsById(termsAndConditionsId);

            learningProvider.removeTermsAndConditions(termsAndConditions);

            learningProviderRepository.save(learningProvider);

            return ResponseEntity.noContent().build();
        }).orElseGet(() -> ResponseEntity.badRequest().build());
    }
}
