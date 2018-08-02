package uk.gov.cslearning.catalogue.domain;

import org.elasticsearch.common.UUIDs;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

public class LearningProvider {

    @Id
    private String id = UUIDs.randomBase64UUID();
    private String name;
    private Long dateCreated;
    private String createdByEmail;
    private List<TermsAndConditions> termsAndConditions = new ArrayList<>();
    private List<CancellationPolicy> cancellationPolicies = new ArrayList<>();

    public LearningProvider(String name, String createdByEmail) {
        this.name = name;
        this.createdByEmail = createdByEmail;
        this.dateCreated = System.currentTimeMillis();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<TermsAndConditions> getTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(List<TermsAndConditions> termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public Long getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Long dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getCreatedByEmail() {
        return createdByEmail;
    }

    public void setCreatedByEmail(String createdByEmail) {
        this.createdByEmail = createdByEmail;
    }

    public List<CancellationPolicy> getCancellationPolicies() {
        return cancellationPolicies;
    }

    public void setCancellationPolicies(List<CancellationPolicy> cancellationPolicies) {
        this.cancellationPolicies = cancellationPolicies;
    }
}

