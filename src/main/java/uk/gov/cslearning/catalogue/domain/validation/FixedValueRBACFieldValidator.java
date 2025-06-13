package uk.gov.cslearning.catalogue.domain.validation;

import lombok.extern.slf4j.Slf4j;
import uk.gov.cslearning.catalogue.Utils;
import uk.gov.cslearning.catalogue.exception.ForbiddenException;

@Slf4j
public class FixedValueRBACFieldValidator <T> extends BasicRBACFieldValidator<T> {

    private final T existingValue;
    private final T newValue;

    public FixedValueRBACFieldValidator(T existingValue, T newValue, RoleSet requiredRoles) {
        super(requiredRoles);
        this.existingValue = existingValue;
        this.newValue = newValue;
    }

    @Override
    public void validate(T existingValue, T newValue) throws ForbiddenException {
        if (existingValue.equals(this.existingValue) && newValue.equals(this.newValue)
        && !Utils.checkRoles(getRequiredRoles())) {
            log.debug("Attempted update from {} to {} without required roles. Required roles are: {}", this.existingValue, this.newValue, getRequiredRoles().toString());
            throw new ForbiddenException();
        }
    }

}
