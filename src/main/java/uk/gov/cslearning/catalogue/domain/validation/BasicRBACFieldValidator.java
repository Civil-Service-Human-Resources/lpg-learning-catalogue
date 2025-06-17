package uk.gov.cslearning.catalogue.domain.validation;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import uk.gov.cslearning.catalogue.Utils;
import uk.gov.cslearning.catalogue.exception.ForbiddenException;

@Getter
@Slf4j
public class BasicRBACFieldValidator<T> implements IRABCFieldValidator<T> {

    private final RoleSet requiredRoles;

    public BasicRBACFieldValidator(RoleSet requiredRoles) {
        this.requiredRoles = requiredRoles;
    }

    @Override
    public void validate(T existingValue, T newValue) throws ForbiddenException {
        if (((existingValue != null && newValue != null) && !existingValue.equals(newValue)) && !Utils.checkRoles(getRequiredRoles())) {
            log.debug("Attempted update from {} to {} without required roles. Required roles are: {}", existingValue, newValue, getRequiredRoles().toString());
            throw new ForbiddenException();
        }
    }

}
