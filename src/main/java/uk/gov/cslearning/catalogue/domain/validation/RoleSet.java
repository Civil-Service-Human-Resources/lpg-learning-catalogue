package uk.gov.cslearning.catalogue.domain.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class RoleSet {

    private final String[] anyRole;
    private final String[] allRoles;

    public static RoleSet anyRoles(String... roles) {
        return new RoleSet(roles, new String[]{});
    }

    public static RoleSet allRoles(String... roles) {
        return new RoleSet(new String[]{}, roles);
    }

    public String toString() {
        return String.format("ANY of the following roles: [%s], ALL of the following roles: [%s]",
                String.join(", ", this.anyRole), String.join(", ",this.allRoles));
    }

}
