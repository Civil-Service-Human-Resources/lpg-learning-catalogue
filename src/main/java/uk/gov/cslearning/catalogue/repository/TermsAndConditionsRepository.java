package uk.gov.cslearning.catalogue.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.catalogue.domain.TermsAndConditions;

@Repository
public interface TermsAndConditionsRepository extends ElasticsearchRepository<TermsAndConditions, String>, ResourceSearchRepository{

}
