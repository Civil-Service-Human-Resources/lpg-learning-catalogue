package uk.gov.cslearning.catalogue.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import uk.gov.cslearning.catalogue.domain.media.Media;

public interface MediaRepository extends ElasticsearchRepository<Media, String> {
}
