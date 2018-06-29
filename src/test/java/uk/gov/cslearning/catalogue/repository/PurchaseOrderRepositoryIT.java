package uk.gov.cslearning.catalogue.repository;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.cslearning.catalogue.domain.PurchaseOrder;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * UserRepository integration test.
 */
@ActiveProfiles({"default", "test"})
@RunWith(SpringRunner.class)
@SpringBootTest
public class PurchaseOrderRepositoryIT {

    @Autowired
    private PurchaseOrderRepository repository;


    @Test
    public void shouldSavePurchaseOrder() {
        PurchaseOrder purchaseOrder = createPurchaseOrder("co", LocalDate.now(), LocalDate.now(), "module1");
        repository.save(purchaseOrder);
        assertThat(purchaseOrder.getId(), notNullValue());
    }

    @Test
    public void shouldFindPurchaseOrder() {

        LocalDate validFrom = LocalDate.now().minusDays(1);
        LocalDate validTo = LocalDate.now().plusDays(1);

        repository.save(createPurchaseOrder("co", validFrom, validTo, "module1", "module2"));
        repository.save(createPurchaseOrder("co", validFrom, validTo, "module1"));
        repository.save(createPurchaseOrder("co", validFrom, validTo, "module3"));

        Optional<PurchaseOrder> found = repository.findFirstByDepartmentAndModulesContainsAndValidFromLessThanEqualAndValidToGreaterThanEqual(
                "co", "module1", LocalDate.now(), LocalDate.now());

        assertThat(found.isPresent(), is(true));
    }

    private PurchaseOrder createPurchaseOrder(String department, LocalDate validFrom, LocalDate validTo, String... modules) {
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setDepartment(department);
        purchaseOrder.setValidFrom(validFrom);
        purchaseOrder.setValidTo(validTo);
        purchaseOrder.setModules(ImmutableSet.copyOf(modules));
        return purchaseOrder;
    }
}
