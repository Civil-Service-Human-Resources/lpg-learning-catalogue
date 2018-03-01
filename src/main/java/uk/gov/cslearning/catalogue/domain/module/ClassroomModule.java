package uk.gov.cslearning.catalogue.domain.module;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.unmodifiableCollection;

@JsonTypeName("classroom")
public class ClassroomModule extends Module {

    private String productCode;

    private BigDecimal price;

    private Collection<Availability> availability;

    public ClassroomModule(String productCode, BigDecimal price) {
        this.productCode = productCode;
        this.price = price;
        this.availability = new HashSet<>();
    }

    public Collection<Availability> getAvailability() {
        return unmodifiableCollection(availability);
    }

    public void addAvailability(Availability availability) {
        checkArgument(availability != null);
        this.availability.add(availability);
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
