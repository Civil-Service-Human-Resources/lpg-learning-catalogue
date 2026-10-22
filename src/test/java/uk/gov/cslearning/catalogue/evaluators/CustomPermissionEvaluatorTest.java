package uk.gov.cslearning.catalogue.evaluators;


import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import uk.gov.cslearning.catalogue.domain.CivilServant.CivilServant;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.Owner.Owner;
import uk.gov.cslearning.catalogue.domain.Roles;
import uk.gov.cslearning.catalogue.service.AuthoritiesService;
import uk.gov.cslearning.catalogue.service.CourseService;
import uk.gov.cslearning.catalogue.service.RegistryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CustomPermissionEvaluatorTest {

    private static final String WRITE_PERMISSION = "write";
    private static final String COURSE_ID = "abc123";

    @Mock
    private Authentication authentication;

    @Mock
    private RegistryService registryService;

    @Mock
    private CourseService courseService;

    @Mock
    private AuthoritiesService authoritiesService;

    @InjectMocks
    private CustomPermissionEvaluator customPermissionEvaluator;

    @Test
    public void shouldReturnFalseIfAuthIsNull() {
        boolean hasPermission = customPermissionEvaluator.hasPermission(null, COURSE_ID, WRITE_PERMISSION);

        assertFalse(hasPermission);
    }

    @Test
    public void shouldReturnFalseIfCourseIdIsNull() {
        boolean hasPermission = customPermissionEvaluator.hasPermission(authentication, null, WRITE_PERMISSION);

        assertFalse(hasPermission);
    }

    @Test
    public void shouldReturnFalseIfObjectIsNotInstanceOfString() {
        Object courseId = new Object();

        boolean hasPermission = customPermissionEvaluator.hasPermission(authentication, courseId, WRITE_PERMISSION);

        assertFalse(hasPermission);
    }

    @Test
    public void shouldReturnFalseIfPermissionNotString() {
        boolean hasPermission = customPermissionEvaluator.hasPermission(authentication, COURSE_ID, 1);

        assertFalse(hasPermission);
    }


    @Test
    public void shouldThrowExceptionIfCourseIsNotPresent() {
        when(courseService.findById(any())).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () ->
                customPermissionEvaluator.hasPermission(authentication, COURSE_ID, WRITE_PERMISSION));
    }


    @Test
    public void shouldReturnFalseIfCourseHasNoOwner() {
        Course course = new Course();
        when(courseService.findById(any())).thenReturn(Optional.of(course));

        boolean hasPermission = customPermissionEvaluator.hasPermission(authentication, COURSE_ID, WRITE_PERMISSION);

        assertFalse(hasPermission);
    }

    @Test
    public void shouldReturnFalseIfAuthHasNoAuthorities() {
        Course course = new Course();
        Owner owner = new Owner();
        course.setOwner(owner);

        when(courseService.findById(any())).thenReturn(Optional.of(course));

        boolean hasPermission = customPermissionEvaluator.hasPermission(authentication, COURSE_ID, WRITE_PERMISSION);

        assertFalse(hasPermission);
    }


    @Test
    public void shouldReturnTrueIfUserHasCslAuthor() {
        Course course = new Course();
        Owner owner = new Owner();
        course.setOwner(owner);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(Roles.CSL_AUTHOR));

        doReturn(authorities).when(authentication).getAuthorities();
        when(courseService.findById(any())).thenReturn(Optional.of(course));

        boolean hasPermission = customPermissionEvaluator.hasPermission(authentication, COURSE_ID, WRITE_PERMISSION);

        assertTrue(hasPermission);
    }

    @Test
    public void shouldReturnTrueIfUserHasLearningManager() {

        Course course = new Course();
        Owner owner = new Owner();
        course.setOwner(owner);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(Roles.LEARNING_MANAGER));

        doReturn(authorities).when(authentication).getAuthorities();
        when(courseService.findById(any())).thenReturn(Optional.of(course));

        boolean hasPermission = customPermissionEvaluator.hasPermission(authentication, COURSE_ID, WRITE_PERMISSION);

        assertTrue(hasPermission);
    }

    @Test
    public void shouldReturnFalseIfUserHasOrgAuthorButDoesntMatch() {
        CivilServant civilServant = new CivilServant();

        Course course = new Course();
        Owner owner = new Owner();
        course.setOwner(owner);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(Roles.ORGANISATION_AUTHOR));

        doReturn(authorities).when(authentication).getAuthorities();
        when(registryService.getCurrentCivilServant()).thenReturn(civilServant);
        when(courseService.findById(any())).thenReturn(Optional.of(course));
        when(authoritiesService.isOrganisationalUnitCodeEqual(civilServant, course.getOwner())).thenReturn(false);

        boolean hasPermission = customPermissionEvaluator.hasPermission(authentication, COURSE_ID, WRITE_PERMISSION);

        assertFalse(hasPermission);
    }

    @Test
    public void shouldReturnTrueIfUserHasOrgAuthorAndMatches() {
        CivilServant civilServant = new CivilServant();

        Course course = new Course();
        Owner owner = new Owner();
        course.setOwner(owner);

        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(Roles.ORGANISATION_AUTHOR));

        doReturn(authorities).when(authentication).getAuthorities();
        when(registryService.getCurrentCivilServant()).thenReturn(civilServant);
        when(courseService.findById(any())).thenReturn(Optional.of(course));
        when(authoritiesService.isOrganisationalUnitCodeEqual(civilServant, course.getOwner())).thenReturn(true);

        boolean hasPermission = customPermissionEvaluator.hasPermission(authentication, COURSE_ID, WRITE_PERMISSION);

        assertTrue(hasPermission);
    }

}
