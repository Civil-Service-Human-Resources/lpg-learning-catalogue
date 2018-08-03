package uk.gov.cslearning.catalogue.domain;

import org.elasticsearch.common.UUIDs;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.validation.constraints.NotNull;

@Document(indexName = "lpg-terms-and-conditions", type = "termsAndConditions")
public class TermsAndConditions {

    @Id
    private String id = UUIDs.randomBase64UUID();

    @NotNull
    private String name;

    @NotNull
    private String description;

    public TermsAndConditions() {
    }

    public TermsAndConditions(@NotNull String name, @NotNull String description) {
        this.name = name;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
