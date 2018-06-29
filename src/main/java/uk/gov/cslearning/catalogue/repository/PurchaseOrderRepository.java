package uk.gov.cslearning.catalogue.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.catalogue.domain.Feedback;
import uk.gov.cslearning.catalogue.domain.PurchaseOrder;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends ElasticsearchRepository<PurchaseOrder, String> {

    Optional<PurchaseOrder> findFirstByDepartmentAndModulesContainsAndValidFromLessThanEqualAndValidToGreaterThanEqual(
            String department, String moduleId, LocalDate validFrom, LocalDate validTo);
}
