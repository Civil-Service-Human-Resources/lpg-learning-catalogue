package uk.gov.cslearning.catalogue.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.domain.CivilServant.CivilServant;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.module.Audience;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class AudienceService {
    private CourseService courseService;
    private AuthoritiesService authoritiesService;
    private RegistryService registryService;

    public AudienceService(CourseService courseService, AuthoritiesService authoritiesService, RegistryService registryService) {
        this.courseService = courseService;
        this.authoritiesService = authoritiesService;
        this.registryService = registryService;
    }

    public Course save(String courseId, Audience audience) {
        Course course = courseService.getCourseById(courseId);

        Set<Audience> audiences = new HashSet<>(course.getAudiences());
        audiences.add(audience);
        course.setAudiences(audiences);

        return courseService.save(course);
    }

    public Course updateAudience(Course course, Audience newAudience, Audience audience) {
        Set<Audience> audiences = new HashSet<>(course.getAudiences());
        audiences.remove(audience);
        audiences.add(newAudience);

        course.setAudiences(audiences);

        return courseService.save(course);
    }

    public Optional<Audience> find(String courseId, String audienceId) {
        Course course = courseService.getCourseById(courseId);

        return find(course, audienceId);
    }

    public Optional<Audience> find(Course course, String audienceId) {
        return course.getAudiences().stream()
                .filter(audience -> audience.getId().equals(audienceId))
                .findFirst();
    }

    public boolean isPermitted(String courseId, Authentication authentication) {
        if (authoritiesService.isCslAuthor(authentication) || authoritiesService.isLearningManager(authentication)) {
            return true;
        }

        Course course = courseService.getCourseById(courseId);

        if (course.getOwner() != null) {
            CivilServant civilServant = registryService.getCurrentCivilServant();

            return isOrganisationPermitted(authentication, course, civilServant) || isProfessionPermitted(authentication, course, civilServant);
        }
        return false;
    }

    private boolean isOrganisationPermitted(Authentication authentication, Course course, CivilServant civilServant) {
        return authoritiesService.isOrgAuthor(authentication) && authoritiesService.isOrganisationalUnitCodeEqual(civilServant, course.getOwner());
    }

    private boolean isProfessionPermitted(Authentication authentication, Course course, CivilServant civilServant) {
        return authoritiesService.isProfessionAuthor(authentication) && authoritiesService.isProfessionIdEqual(civilServant, course.getOwner());
    }

    public Audience setDefaults(Authentication authentication, Audience audience) {
        CivilServant civilServant = registryService.getCurrentCivilServant();

        if (authoritiesService.isOrgAuthor(authentication)) {
            civilServant.getOrganisationalUnitCode().ifPresent(organisationalUnitCode -> audience.setDepartments(new HashSet<>(Arrays.asList(organisationalUnitCode))));
        } else if (authoritiesService.isProfessionAuthor(authentication)) {
            civilServant.getProfessionName().ifPresent(professionName -> audience.setAreasOfWork(new HashSet<>(Arrays.asList(professionName))));
        }
        return audience;
    }
}
