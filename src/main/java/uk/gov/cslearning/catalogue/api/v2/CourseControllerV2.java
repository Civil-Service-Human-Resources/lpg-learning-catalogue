package uk.gov.cslearning.catalogue.api.v2;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.cslearning.catalogue.api.PageResults;
import uk.gov.cslearning.catalogue.api.v2.model.GetCoursesParameters;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.module.Audience;
import uk.gov.cslearning.catalogue.repository.CourseRepository;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.util.CollectionUtils.containsAny;

@RestController
@RequestMapping("/v2/courses")
public class CourseControllerV2 {

    private final CourseRepository courseRepository;

    @Autowired
    public CourseControllerV2(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @GetMapping
    public ResponseEntity<PageResults<Course>> list(GetCoursesParameters parameters) {
        Page<Course> results = courseRepository.findSuggested(parameters);

//        List<Profession> otherAreasOfWork = civilServant.getOtherAreasOfWork();
//        String professionName = civilServant.getProfessionName().get();
//
//        List<String> otherAreasOfWorkNames = otherAreasOfWork.stream()
//                .map(Profession::getName)
//                .collect(collectingAndThen(toList(), this::listWithNone));
//
//        List<Interest> interests_cs = civilServant.getInterests();
//
//        List<String> interestNames = interests_cs.stream()
//                .map(Interest::getName)
//                .collect(collectingAndThen(toList(), this::listWithNone));

        ArrayList<Course> filteredCourses = new ArrayList<>();

        for (Course course : results) {
            if (!parameters.getExcludeCourseIDs().contains(course.getId())) {
                for (Audience audience : course.getAudiences()) {
                    if (!audience.isRequiredForDepartments(parameters.getDepartments())
                    && !containsAny(audience.getAreasOfWork(), parameters.getExcludeAreasOfWork())
                    && !containsAny(audience.getInterests(), parameters.getExcludeInterests())
                    && !containsAny(audience.getDepartments(), parameters.getExcludeDepartments())) {
                        filteredCourses.add(course);
                    }
                }
            }
        }
////
//        for (Course course : results) {
//            for (Audience audience : course.getAudiences()) {
//                for (String organisation : parameters.getDepartments()) {
//                    // any course that has a dept defined (check if AOW and Interests are also part of audience).
//                    if (audience.getDepartments().contains(organisation) && audience.getGrades().contains(parameters.getGrade())
//                            && isAreaOfWorkValid(audience, parameters.getExcludeAreasOfWork())
//                            && (audience.getInterests().isEmpty() || containsAny(audience.getInterests(), parameters.getExcludeInterests()))) {
//                        filteredCourses.add(course);
//                    }
//                }
//
//                // Show any courses with AOW (audience could also have a dept/interest so filter it if it does
//                // ie, if your dept is CO and it has been flagged as required for CO, you would not want it appearing here for you also...
//                if (audience.getAreasOfWork().contains(parameters.getAreaOfWork()) && audience.getGrades().contains(parameters.getGrade())
//                        && (audience.getDepartments().isEmpty() || containsAny(audience.getDepartments(), parameters.getDepartments()))
//                        && (audience.getInterests().isEmpty() || containsAny(audience.getInterests(), parameters.getExcludeInterests()))) {
//                    filteredCourses.add(course);
//                }
//
//                // Show any courses with Interest (audience could also have a dept/aow so filter it if it does
//                // ie, if your dept is CO and it has been flagged as required for CO, you would not want it appearing here for you also...
//                if (audience.getInterests().contains(parameters.getInterest()) && audience.getGrades().contains(parameters.getGrade())
//                        && (audience.getDepartments().isEmpty() || containsAny(audience.getDepartments(),parameters.getDepartments()))
//                        && isAreaOfWorkValid(audience, parameters.getExcludeAreasOfWork())) {
//                    filteredCourses.add(course);
//                }
//            }
//        }
//
        Set<Course> set = new LinkedHashSet<>(filteredCourses);
        filteredCourses.clear();
        filteredCourses.addAll(set);

        results = new PageImpl<>(filteredCourses, parameters.getPageable(), filteredCourses.size());

        return ResponseEntity.ok(new PageResults<>(results, parameters.getPageable()));
    }

    private boolean isAreaOfWorkValid(Audience audience, List<String> professions) {
        return (audience.getAreasOfWork().isEmpty() || ((CollectionUtils.containsAny(audience.getAreasOfWork(), professions))));
    }
}
