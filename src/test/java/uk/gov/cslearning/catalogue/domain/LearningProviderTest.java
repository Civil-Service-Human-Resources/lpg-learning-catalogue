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

        assertEquals(LEARNING_PROVIDER_NAME, learningProvider.getName());
    }

    @Test
    public void shouldAddCancellationPolicyToLearningProvider() {
        LearningProvider learningProvider = new LearningProvider(LEARNING_PROVIDER_NAME);

        CancellationPolicy cancellationPolicy = new CancellationPolicy("Example cancellation", "Short", "Full");

        assertEquals(0, learningProvider.getCancellationPolicies().size());

        learningProvider.addCancellationPolicy(cancellationPolicy);

        assertEquals(1, learningProvider.getCancellationPolicies().size());
    }

    @Test
    public void shouldGetCancellationPolicyById() {
        LearningProvider learningProvider = new LearningProvider(LEARNING_PROVIDER_NAME);

        CancellationPolicy cancellationPolicy = new CancellationPolicy("Example cancellation", "Short", "Full");
        String id = cancellationPolicy.getId();

        learningProvider.addCancellationPolicy(cancellationPolicy);

        assertEquals(cancellationPolicy, learningProvider.getCancellationPolicyById(id));
    }

    @Test
    public void shouldRemoveCancellationPolicy() {
        LearningProvider learningProvider = new LearningProvider(LEARNING_PROVIDER_NAME);

        CancellationPolicy cancellationPolicy = new CancellationPolicy("Example cancellation", "Short", "Full");

        learningProvider.addCancellationPolicy(cancellationPolicy);
        assertEquals(1, learningProvider.getCancellationPolicies().size());

        learningProvider.removeCancellationPolicy(cancellationPolicy);

        assertEquals(0, learningProvider.getCancellationPolicies().size());
    }

    @Test
    public void shouldAddTermsAndConditionsToLearningProvider() {
        LearningProvider learningProvider = new LearningProvider(LEARNING_PROVIDER_NAME);

        TermsAndConditions termsAndConditions = new TermsAndConditions("Example terms", "Full");

        assertEquals(0, learningProvider.getTermsAndConditions().size());

        learningProvider.addTermsAndConditions(termsAndConditions);

        assertEquals(1, learningProvider.getTermsAndConditions().size());
    }

    @Test
    public void shouldGetTermsAndConditionsById() {
        LearningProvider learningProvider = new LearningProvider(LEARNING_PROVIDER_NAME);

        TermsAndConditions termsAndConditions = new TermsAndConditions("Example terms", "Full");
        String id = termsAndConditions.getId();

        learningProvider.addTermsAndConditions(termsAndConditions);

        assertEquals(termsAndConditions, learningProvider.getTermsAndConditionsById(id));
    }

    @Test
    public void shouldRemoveTermsAndConditions() {
        LearningProvider learningProvider = new LearningProvider(LEARNING_PROVIDER_NAME);

        TermsAndConditions termsAndConditions = new TermsAndConditions("Example terms", "Full");

        learningProvider.addTermsAndConditions(termsAndConditions);
        assertEquals(1, learningProvider.getTermsAndConditions().size());

        learningProvider.removeTermsAndConditions(termsAndConditions);

        assertEquals(0, learningProvider.getTermsAndConditions().size());
    }
}
