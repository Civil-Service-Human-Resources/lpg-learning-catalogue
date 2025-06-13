package uk.gov.cslearning.catalogue.domain.validation;

import lombok.Getter;
import uk.gov.cslearning.catalogue.Utils;
import uk.gov.cslearning.catalogue.exception.ForbiddenException;

@Getter
public class BasicRBACFieldValidator<T> implements IRABCFieldValidator<T> {

    private final RoleSet requiredRoles;

    public BasicRBACFieldValidator(RoleSet requiredRoles) {
        this.requiredRoles = requiredRoles;
    }

    @Override
    public void validate(T existingValue, T newValue) throws ForbiddenException {
        if (!existingValue.equals(newValue) && !Utils.checkRoles(getRequiredRoles())) {
            throw new ForbiddenException();
        }
    }

}
