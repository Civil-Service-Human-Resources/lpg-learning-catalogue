package uk.gov.cslearning.catalogue.domain.validation;

import uk.gov.cslearning.catalogue.exception.ForbiddenException;

public interface IRABCFieldValidator<T> {

    void validate(T existingValue, T newValue) throws ForbiddenException;
    RoleSet getRequiredRoles();
}
