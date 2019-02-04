package uk.gov.cslearning.catalogue.evaluators;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.domain.CivilServant.CivilServant;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.service.AuthoritiesService;
import uk.gov.cslearning.catalogue.service.CourseService;
import uk.gov.cslearning.catalogue.service.RegistryService;

import java.io.Serializable;
import java.util.function.Supplier;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomPermissionEvaluator.class);
    private static final String ROLE_CSL_AUTHOR = "CSL_AUTHOR";
    private static final String ROLE_ORGANISATION_AUTHOR = "ORGANISATION_AUTHOR";
    private static final String ROLE_PROFESSION_AUTHOR = "PROFESSION_AUTHOR";
    private static final String ROLE_SUPPLIER_AUTHOR = "SUPPLIER_AUTHOR";
    private static final String ROLE_LEARNING_MANAGER = "LEARNING_MANAGER";

    private RegistryService registryService;

    private CourseService courseService;

    private AuthoritiesService authoritiesService;

    public CustomPermissionEvaluator(RegistryService registryService, CourseService courseService, AuthoritiesService authoritiesService) {
        this.registryService = registryService;
        this.courseService = courseService;
        this.authoritiesService = authoritiesService;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
        if ((auth == null) || (targetDomainObject == null) || !(targetDomainObject instanceof String) || !(permission instanceof String) || !(targetDomainObject instanceof String)) {
            return false;
        }

        String id = (String) targetDomainObject;

        return hasScope(auth, id);
    }

    private boolean hasScope(Authentication auth, String id) {
        CivilServant civilServant = registryService.getCurrentCivilServant();
        civilServant.setSupplier(authoritiesService.getSupplier(auth));

        Course course = courseService.findById(id).orElseThrow((Supplier<IllegalStateException>) () -> {
            throw new IllegalStateException(
                    "Cannot get course");
        });

        if (course.getOwner() == null) {
            return false;
        }

        for (GrantedAuthority grantedAuth : auth.getAuthorities()) {
            LOGGER.info("User has authority: {}", grantedAuth.getAuthority());

            if (grantedAuth.getAuthority().equals(ROLE_CSL_AUTHOR)) {
                return true;
            }
            if (grantedAuth.getAuthority().equals(ROLE_LEARNING_MANAGER)) {
                return true;
            }
            if (grantedAuth.getAuthority().equals(ROLE_ORGANISATION_AUTHOR) && authoritiesService.isOrganisationalUnitCodeEqual(civilServant, course.getOwner())) {
                return true;
            }
            if (grantedAuth.getAuthority().equals(ROLE_PROFESSION_AUTHOR) && authoritiesService.isProfessionIdEqual(civilServant, course.getOwner())) {
                return true;
            }
            if (grantedAuth.getAuthority().equals(ROLE_SUPPLIER_AUTHOR)) {
                return true;
            }
        }
        return false;
    }
}