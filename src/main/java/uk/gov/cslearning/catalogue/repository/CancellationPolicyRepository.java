package uk.gov.cslearning.catalogue.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.catalogue.domain.CancellationPolicy;

@Repository
public interface CancellationPolicyRepository extends ElasticsearchRepository<CancellationPolicy, String> {

}
