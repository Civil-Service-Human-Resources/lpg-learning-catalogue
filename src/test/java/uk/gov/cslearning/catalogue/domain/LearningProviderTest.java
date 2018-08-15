package uk.gov.cslearning.catalogue.domain;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
public class LearningProviderTest {

    public static final String LEARNING_PROVIDER_NAME = "Example learning provider";

    @Test
    public void shouldGetLearningProviderName() {
        LearningProvider learningProvider = new LearningProvider(LEARNING_PROVIDER_NAME);

        assertEquals(learningProvider.getName(), LEARNING_PROVIDER_NAME);
    }

    @Test
    public void shouldAddCancellationPolicyToLearningProvider() {
        LearningProvider learningProvider = new LearningProvider(LEARNING_PROVIDER_NAME);

        CancellationPolicy cancellationPolicy = new CancellationPolicy("Example cancellation", "Short", "Full");

        assertEquals(learningProvider.getCancellationPolicies().size(), 0);

        learningProvider.addCancellationPolicy(cancellationPolicy);

        assertEquals(learningProvider.getCancellationPolicies().size(), 1);
    }

    @Test
    public void shouldAddTermsAndConditionsToLearningProvider() {
        LearningProvider learningProvider = new LearningProvider(LEARNING_PROVIDER_NAME);

        TermsAndConditions termsAndConditions = new TermsAndConditions("Example terms", "Full");

        assertEquals(learningProvider.getTermsAndConditions().size(), 0);

        learningProvider.addTermsAndConditions(termsAndConditions);

        assertEquals(learningProvider.getTermsAndConditions().size(), 1);
    }
}
