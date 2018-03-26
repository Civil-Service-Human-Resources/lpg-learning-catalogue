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
import uk.gov.cslearning.catalogue.domain.Feedback;
import uk.gov.cslearning.catalogue.repository.FeedbackRepository;

import static com.google.common.base.Preconditions.checkArgument;

@RestController
@RequestMapping("/feedback")
public class FeedbackController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FeedbackController.class);

    private FeedbackRepository feedbackRepository;

    @Autowired
    public FeedbackController(FeedbackRepository feedbackRepository) {
        checkArgument(feedbackRepository != null);
        this.feedbackRepository = feedbackRepository;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody Feedback feedback, UriComponentsBuilder builder) {
        LOGGER.debug("Creating feedback {}", feedback);
        Feedback newFeedback = feedbackRepository.save(feedback);
        return ResponseEntity.created(builder.path("/feedback/{feedbackId}").build(newFeedback.getId())).build();
    }
}
