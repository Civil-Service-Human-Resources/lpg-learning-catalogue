package uk.gov.cslearning.catalogue.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.domain.CivilServant.CivilServant;
import uk.gov.cslearning.catalogue.domain.Owner.Owner;
import uk.gov.cslearning.catalogue.domain.Scope;
import uk.gov.cslearning.catalogue.domain.Supplier;

@Service
public class AuthoritiesService {

    public static final String ROLE_CSL_AUTHOR = "CSL_AUTHOR";
    public static final String ROLE_ORGANISATION_AUTHOR = "ORGANISATION_AUTHOR";
    public static final String ROLE_PROFESSION_AUTHOR = "PROFESSION_AUTHOR";
    public static final String ROLE_LEARNING_MANAGER = "LEARNING_MANAGER";
    public static final String ROLE_KPMG_SUPPLIER_AUTHOR = "KPMG_SUPPLIER_AUTHOR";
    public static final String ROLE_KORNFERRY_SUPPLIER_AUTHOR = "KORNFERRY_SUPPLIER_AUTHOR";
    public static final String ROLE_KNOWLEDGEPOOL_SUPPLIER_AUTHOR = "KNOWLEDGEPOOL_SUPPLIER_AUTHOR";


    public Scope getScope(Authentication authentication) {
        return isCslAuthor(authentication) || isLearningManager(authentication) ? Scope.GLOBAL : Scope.LOCAL;
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

    public boolean isSupplierEqual(Authentication authentication, Owner owner) {
        return (getSupplier(authentication).equals(owner.getSupplier()));
    }

    public String getSupplier(Authentication authentication) {
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(ROLE_KPMG_SUPPLIER_AUTHOR))) {
            return Supplier.KPMG.name();
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(ROLE_KORNFERRY_SUPPLIER_AUTHOR))) {
            return Supplier.KORNFERRY.name();
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority(ROLE_KNOWLEDGEPOOL_SUPPLIER_AUTHOR))) {
            return Supplier.KNOWLEDGEPOOL.name();
        } else return "";
    }
}