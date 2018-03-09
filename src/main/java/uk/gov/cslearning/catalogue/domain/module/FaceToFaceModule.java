package uk.gov.cslearning.catalogue.domain.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.unmodifiableCollection;

@JsonTypeName("face-to-face")
public class FaceToFaceModule extends Module {

    private String productCode;

    private Collection<Availability> availability;

    @JsonCreator
    public FaceToFaceModule(@JsonProperty("productCode") String productCode) {
        this.productCode = productCode;
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
}
