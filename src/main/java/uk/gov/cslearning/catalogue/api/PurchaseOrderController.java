package uk.gov.cslearning.catalogue.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.catalogue.domain.PurchaseOrder;
import uk.gov.cslearning.catalogue.repository.PurchaseOrderRepository;

import java.time.LocalDate;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

@RestController
@RequestMapping("/purchase-orders")
public class PurchaseOrderController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurchaseOrderController.class);

    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    public PurchaseOrderController(PurchaseOrderRepository purchaseOrderRepository) {
        checkArgument(purchaseOrderRepository != null);
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody PurchaseOrder purchaseOrder, UriComponentsBuilder builder) {
        LOGGER.debug("Creating purchaseOrder {}", purchaseOrder);
        PurchaseOrder newPurchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        return ResponseEntity.created(builder
                .path("/purchase-orders/{purchaseOrderId}")
                .build(newPurchaseOrder.getId()))
                .build();
    }

    @GetMapping
    public ResponseEntity<Iterable<PurchaseOrder>> listAll() {
        LOGGER.debug("Listing all purchase orders");

        Iterable<PurchaseOrder> result = purchaseOrderRepository.findAll();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping(params = {"department", "moduleId"})
    public ResponseEntity<PurchaseOrder> find(@RequestParam("department") String department,
                                              @RequestParam("moduleId") String moduleId) {
        LOGGER.debug("Finding purchaseOrder for department {} and moduleId {}", department, moduleId);

        Optional<PurchaseOrder> result = purchaseOrderRepository
                .findFirstByDepartmentAndModulesContainsAndValidFromLessThanEqualAndValidToGreaterThanEqual(
                        department, moduleId, LocalDate.now(), LocalDate.now());
        return result
                .map(purchaseOrder -> new ResponseEntity<>(purchaseOrder, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseOrder> get(@PathVariable String id) {
        LOGGER.debug("Finding purchaseOrder with id {}", id);

        Optional<PurchaseOrder> result = purchaseOrderRepository.findById(id);
        return result
                .map(purchaseOrder -> new ResponseEntity<>(purchaseOrder, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
