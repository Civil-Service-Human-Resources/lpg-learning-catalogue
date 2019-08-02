package uk.gov.cslearning.catalogue.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.gov.cslearning.catalogue.api.FilterParameters;
import uk.gov.cslearning.catalogue.api.OwnerParameters;
import uk.gov.cslearning.catalogue.api.ProfileParameters;
import uk.gov.cslearning.catalogue.domain.Course;
import uk.gov.cslearning.catalogue.domain.SearchPage;
import uk.gov.cslearning.catalogue.domain.Status;

import java.util.Collection;
import java.util.List;

public interface CourseSearchRepository {
    SearchPage search(String query, Pageable pageable, FilterParameters filterParameters, Collection<Status> status, OwnerParameters ownerParameters, ProfileParameters profileParameters, String visbility);

    Page<Course> findAllByOrganisationCode(String organisationalUnitCode, Pageable pageable);

    Page<Course> findAllByProfessionId(String professionId, Pageable pageable);

    Page<Course> findAllBySupplier(String supplier, Pageable pageable);

    List<Course> findAllByOrganisationCodeAsList(String organisationalUnitCode);

    List<Course> findAllByProfessionIdAsList(String professionId);

    List<Course> findAllBySupplierAsList(String supplier);
}
