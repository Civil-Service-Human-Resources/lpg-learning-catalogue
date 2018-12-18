package uk.gov.cslearning.catalogue.dto.factory;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.domain.LearningProvider;
import uk.gov.cslearning.catalogue.dto.LearningProviderDto;

@Component
public class LearningProviderDtoFactory {
    public LearningProviderDto create(LearningProvider learningProvider) {
        LearningProviderDto learningProviderDto = new LearningProviderDto();
        learningProviderDto.setId(learningProvider.getId());
        learningProviderDto.setName(learningProvider.getName());

        return learningProviderDto;
    }
}
