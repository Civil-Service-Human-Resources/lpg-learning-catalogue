package uk.gov.cslearning.catalogue.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/policy")
public class PolicyController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PolicyController.class);
//    private final PolicyRepository policyRepository;

    @Autowired
    public PolicyController() {
    }

//    @PostMapping
//    public ResponseEntity<Void> create(@RequestBody Policy policy, UriComponentsBuilder builder) {
//        LOGGER.debug("Creating Policy {}", policy.toString());
//
//        Policy newPolicy = policyRepository.save(policy);
//
//        return ResponseEntity.created(builder.path("/policy/}").build(newPolicy.getId())).build();
//    }
//
//    @PutMapping(path = "/{policyId}")
//    public ResponseEntity<Void> update(@PathVariable("policyId") String policyId, @RequestBody Policy policy) {
//        LOGGER.debug("Updating Policy {}", policy.toString());
//
//        if (!policyId.equals(policy.getId())) {
//            return ResponseEntity.badRequest().build();
//        }
//        if (!policyRepository.existsById(policyId)) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        policyRepository.save(policy);
//
//        return ResponseEntity.noContent().build();
//    }
}
