package uk.gov.cslearning.catalogue.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.service.AuthenticationFacade;

@Repository
public class AuditableCourseRepository extends AuditableRepository<Course, CourseRepository> {

    @Autowired
    public AuditableCourseRepository(CourseRepository wrappedRepository, AuthenticationFacade authenticationFacade) {
        super(wrappedRepository, authenticationFacade);
    }

    public Page<Course> findMandatory(String department, Pageable pageRequest) {
        return super.wrappedRepository.findMandatory(department, pageRequest);
    }

    public Page<Course> findSuggested(String department, String areaOfWork, String interest, Pageable pageable) {
        return super.wrappedRepository.findSuggested(department, areaOfWork, interest, pageable);
    }
}
