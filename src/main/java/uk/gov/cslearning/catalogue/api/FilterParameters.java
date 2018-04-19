package uk.gov.cslearning.catalogue.api;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class FilterParameters {

    private String types;

    private String cost;

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getType() {
        return types;
    }

    public void setType(String type) {
        this.types = type;
    }


}
