package uk.gov.cslearning.catalogue.domain.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("elearning")
public class ELearningModule extends Module {

    private String startPage;

    private String url;

    @JsonCreator
    public ELearningModule(@JsonProperty("startPage") String startPage, @JsonProperty("url") String url) {
        setStartPage(startPage);
        setUrl(url);
    }

    public String getStartPage() {
        return startPage;
    }

    public void setStartPage(String startPage) {
        this.startPage = startPage;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getModuleType() {
        return "elearning";
    }
}
