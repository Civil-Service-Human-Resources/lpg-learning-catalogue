package uk.gov.cslearning.catalogue.domain.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Collection;
import java.util.HashSet;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.unmodifiableCollection;

@JsonTypeName("face-to-face")
public class FaceToFaceModule extends Module {

    private String productCode;

    private Collection<Event> events;

    @JsonCreator
    public FaceToFaceModule(@JsonProperty("productCode") String productCode) {
        this.productCode = productCode;
        this.events = new HashSet<>();
    }

    public Collection<Event> getEvents() {
        return unmodifiableCollection(events);
    }

    public void setEvents(Collection<Event> events) {
        this.events.clear();
        if (events != null) {
            this.events.addAll(events);
        }
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
}
