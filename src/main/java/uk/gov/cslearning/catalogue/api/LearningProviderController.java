package uk.gov.cslearning.catalogue.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.catalogue.domain.LearningProvider;
import uk.gov.cslearning.catalogue.repository.LearningProviderRepository;

import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/learning-provider")
public class LearningProviderController {
    private static final Logger LOGGER = LoggerFactory.getLogger(LearningProviderController.class);

    private final LearningProviderRepository learningProviderRepository;

    @Autowired
    public LearningProviderController(LearningProviderRepository learningProviderRepository) {
        this.learningProviderRepository = learningProviderRepository;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody LearningProvider learningProvider, UriComponentsBuilder builder) {
        LOGGER.debug("Creating Learning Provider {}", learningProvider.toString());

        LearningProvider newLearningProvider = learningProviderRepository.save(learningProvider);

        return ResponseEntity.created(builder.path("/learning-provider/{learningProviderId}").build(newLearningProvider.getId())).build();
    }

    @GetMapping("/{learningProviderId}")
    public ResponseEntity<LearningProvider> get(@PathVariable("learningProviderId") String learningProviderId) {
        LOGGER.debug("Getting Learning Provider with Id {}", learningProviderId);

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

    @GetMapping(path = "/list")
    public ResponseEntity<PageResults<LearningProvider>> list(
            PageParameters pageParameters) {
        LOGGER.debug("Listing Learning Providers with {}", pageParameters.toString());

        Pageable pageable = pageParameters.getPageRequest();
        Page<LearningProvider> page;

        page = learningProviderRepository.findAll(pageable);

        return ResponseEntity.ok(new PageResults<>(page, pageable));
    }
//
//    @PostMapping(path = "/{learningProviderId}/cancellation-policies")
//    public ResponseEntity<Object> addCancellationPolicyToLearningProvider(@PathVariable("learningProviderId") String learningProviderId, @RequestBody Policy cancellationPolicy) {
//        LOGGER.debug("Updating Learning Provider with Id {}", learningProviderId);
//
//        if (!learningProviderRepository.existsById(learningProviderId)) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        Optional<LearningProvider> result = learningProviderRepository.findById(learningProviderId);
//
//        return result.map(learningProvider -> {
//            learningProvider.addCancellationPolicy(cancellationPolicy);
//
//            learningProviderRepository.save(learningProvider);
//
//            return ResponseEntity.noContent().build();
//        }).orElseGet(() -> ResponseEntity.badRequest().build());
//    }
//
////    edit
////        @PostMapping(path = "/{learningProviderId}/cancellation-policies/{cancellationid}")
//                learningProvider.getCancellationPolicy(cancellationPolicyId);

}
