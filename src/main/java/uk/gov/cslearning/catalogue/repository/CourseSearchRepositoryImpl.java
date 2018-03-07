package uk.gov.cslearning.catalogue.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.catalogue.domain.Course;

import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Repository
public class CourseSearchRepositoryImpl implements CourseSearchRepository {

    private static Logger LOGGER = LoggerFactory.getLogger(CourseSearchRepositoryImpl.class);

    private ElasticsearchTemplate template;

    public CourseSearchRepositoryImpl(ElasticsearchTemplate template) {
        checkArgument(template != null);
        this.template = template;
    }

    @Override
    public List<Course> search(String query) {
        LOGGER.debug("Executing search query for {}", query);
        return null;
    }
}
