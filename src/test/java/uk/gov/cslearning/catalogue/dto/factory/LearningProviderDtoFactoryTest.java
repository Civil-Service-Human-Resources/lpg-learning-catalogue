package uk.gov.cslearning.catalogue.dto.factory;

import org.junit.Test;
import uk.gov.cslearning.catalogue.domain.LearningProvider;
import uk.gov.cslearning.catalogue.dto.LearningProviderDto;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LearningProviderDtoFactoryTest {

    private LearningProviderDtoFactory dtoFactory = new LearningProviderDtoFactory();

    @Test
    public void shouldReturnLearningProviderDto() {
        String name = "learning-provider-name";
        LearningProvider learningProvider = new LearningProvider(name);

        LearningProviderDto dto = dtoFactory.create(learningProvider);

        assertNotNull(dto.getId());
        assertEquals(learningProvider.getId(), dto.getId());
        assertEquals(name, dto.getName());
    }
}