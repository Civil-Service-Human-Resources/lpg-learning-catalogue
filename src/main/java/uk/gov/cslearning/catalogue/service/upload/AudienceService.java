package uk.gov.cslearning.catalogue.service.upload;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.module.Audience;
import uk.gov.cslearning.catalogue.repository.CourseRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

@Service
public class AudienceService {
    private final CourseRepository courseRepository;

    public AudienceService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public void save(String courseId, Audience audience) {
        Course course = courseRepository.findById(courseId).orElseThrow((Supplier<IllegalStateException>) () -> {
            throw new IllegalStateException(
                    String.format("Unable to add audience. Course does not exist: %s", courseId));
        });

        Set<Audience> audiences = new HashSet<>(course.getAudiences());
        audiences.add(audience);
        course.setAudiences(audiences);
        courseRepository.save(course);
    }

    public Optional<Audience> find(String courseId, String audienceId) {
        Course course = courseRepository.findById(courseId).orElseThrow((Supplier<IllegalStateException>) () -> {
            throw new IllegalStateException(
                    String.format("Unable to find audience: %s. Course does not exist: %s", audienceId, courseId));
        });

        return find(course, audienceId);
    }

    public Optional<Audience> find(Course course, String audienceId) {
        return course.getAudiences().stream()
                .filter(audience -> audience.getId().equals(audienceId))
                .findFirst();
    }
}
