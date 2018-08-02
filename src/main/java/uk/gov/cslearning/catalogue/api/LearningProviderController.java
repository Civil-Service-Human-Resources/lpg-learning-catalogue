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
import uk.gov.cslearning.catalogue.domain.LearningProvider;
import uk.gov.cslearning.catalogue.repository.LearningProviderRepository;

@RestController
@RequestMapping("/learning-provider")
public class LearningProviderController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CourseController.class);
    private final LearningProviderRepository learningProviderRepository;

    @Autowired
    public LearningProviderController(LearningProviderRepository learningProviderRepository) {
        this.learningProviderRepository = learningProviderRepository;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody LearningProvider learningProvider, UriComponentsBuilder builder) {
        LOGGER.debug("Creating course {}", learningProvider);
        LearningProvider newLearningProvider = learningProviderRepository.save(learningProvider);

        return ResponseEntity.created(builder.path("/learning-provider/{courseId}").build(newLearningProvider.getId())).build();
    }
}
