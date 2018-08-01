package uk.gov.cslearning.catalogue.repository;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import uk.gov.cslearning.catalogue.domain.PurchaseOrder;

@Repository
public interface PurchaseOrderRepository extends ElasticsearchRepository<PurchaseOrder, String> {

    Iterable<PurchaseOrder> findByDepartmentAndModulesContains(String department, String moduleId);
}
