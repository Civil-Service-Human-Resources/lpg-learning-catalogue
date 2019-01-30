package uk.gov.cslearning.catalogue.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.catalogue.domain.LearningProvider;

import java.util.Optional;

@Repository
public interface LearningProviderRepository extends ElasticsearchRepository<LearningProvider, String> {

    Optional<LearningProvider> findByName(String name);
}
