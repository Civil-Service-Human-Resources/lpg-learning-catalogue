package uk.gov.cslearning.catalogue.domain.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import static com.google.common.base.Preconditions.checkArgument;

@JsonTypeName("elearning")
public class ELearningModule extends Module {

    private String startPage;

    @JsonCreator
    public ELearningModule(@JsonProperty("startPage") String startPage) {
        setStartPage(startPage);
    }

    public String getStartPage() {
        return startPage;
    }

    public void setStartPage(String startPage) {
//        checkArgument(startPage != null);
        this.startPage = startPage;
    }
}
