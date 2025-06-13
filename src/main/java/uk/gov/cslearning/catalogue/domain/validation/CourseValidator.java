package uk.gov.cslearning.catalogue.domain.validation;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.Roles;
import uk.gov.cslearning.catalogue.domain.Status;

import java.util.Arrays;
import java.util.List;

@Service
public class CourseValidator {

    private final List<IRABCFieldValidator<Status>> STATUS_VALIDATION_RULES = Arrays.asList(
            new FixedValueRBACFieldValidator<>(Status.DRAFT, Status.PUBLISHED, RoleSet.anyRoles(Roles.LEARNING_PUBLISH, Roles.LEARNING_MANAGER, Roles.CSL_AUTHOR)),
            new FixedValueRBACFieldValidator<>(Status.PUBLISHED, Status.ARCHIVED, RoleSet.anyRoles(Roles.LEARNING_ARCHIVE, Roles.LEARNING_MANAGER, Roles.CSL_AUTHOR)),
            new FixedValueRBACFieldValidator<>(Status.ARCHIVED, Status.DRAFT, RoleSet.anyRoles(Roles.LEARNING_UNARCHIVE, Roles.LEARNING_MANAGER, Roles.CSL_AUTHOR))
    );

    private final IRABCFieldValidator<String> COURSE_DETAILS_VALIDATOR_RULE = new BasicRBACFieldValidator<>(RoleSet.anyRoles(Roles.LEARNING_EDIT, Roles.LEARNING_MANAGER, Roles.CSL_AUTHOR));

    public void validate(Course existingCourse, Course newCourse) {
        STATUS_VALIDATION_RULES.forEach(rule -> rule.validate(existingCourse.getStatus(), newCourse.getStatus()));
        COURSE_DETAILS_VALIDATOR_RULE.validate(existingCourse.getTitle(), newCourse.getTitle());
        COURSE_DETAILS_VALIDATOR_RULE.validate(existingCourse.getDescription(), newCourse.getDescription());
    }
}
