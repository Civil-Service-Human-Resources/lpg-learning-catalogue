package uk.gov.cslearning.catalogue.api;

import lombok.Data;

import java.util.List;

@Data
public class FilterParameters {

    private List<String> types;
    private List<String> departments;
    private List<String> interests;
    private List<String> areasOfWork;
    private String cost;

    public boolean hasAudienceFields(){
        return !departments.isEmpty() || !areasOfWork.isEmpty() || !interests.isEmpty();
    }

    public boolean costIsFree(){
        return this.cost != null && this.cost.equals("free");
    }

    public boolean hasTypes() {
        return this.types != null && !this.types.isEmpty();
    }
}
