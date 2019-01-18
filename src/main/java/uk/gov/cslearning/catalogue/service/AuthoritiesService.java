package uk.gov.cslearning.catalogue.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.domain.CivilServant.CivilServant;
import uk.gov.cslearning.catalogue.domain.Owner.Owner;
import uk.gov.cslearning.catalogue.domain.Scope;

@Service
public class AuthoritiesService {

    public static final String ROLE_CSL_AUTHOR = "CSL_AUTHOR";
    public static final String ROLE_ORGANISATION_AUTHOR = "ORGANISATION_AUTHOR";
    public static final String ROLE_PROFESSION_AUTHOR = "PROFESSION_AUTHOR";
    public static final String ROLE_LEARNING_MANAGER = "LEARNING_MANAGER";

    public Scope getScope(Authentication authentication) {
        return isCslAuthor(authentication) ? Scope.GLOBAL : Scope.LOCAL;
    }

    public boolean isLearningManager(Authentication authentication) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority(ROLE_LEARNING_MANAGER));
    }

    public boolean isCslAuthor(Authentication authentication) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority(ROLE_CSL_AUTHOR));
    }

    public boolean isOrgAuthor(Authentication authentication) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority(ROLE_ORGANISATION_AUTHOR));
    }

    public boolean isProfessionAuthor(Authentication authentication) {
        return authentication.getAuthorities().contains(new SimpleGrantedAuthority(ROLE_PROFESSION_AUTHOR));
    }

    public boolean isOrganisationalUnitCodeEqual(CivilServant civilServant, Owner owner) {
        return civilServant.getOrganisationalUnitCode().map(code -> code.equals(owner.getOrganisationalUnit())).orElse(false);
    }

    public boolean isProfessionIdEqual(CivilServant civilServant, Owner owner) {
        return civilServant.getProfessionId().map(professionId -> professionId.equals(owner.getProfession())).orElse(false);
    }

    public boolean isLearningProviderIdEqual(CivilServant civilServant, Owner owner) {
        return civilServant.getLearningProviderId().map(learningProviderid -> learningProviderid.equals(owner.getLearningProvider())).orElse(false);
    }
}