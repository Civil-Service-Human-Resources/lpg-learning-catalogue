package uk.gov.cslearning.catalogue.domain;

import org.elasticsearch.common.UUIDs;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

@Document(indexName = "lpg-learning-providers", type = "learningProvider")
public class LearningProvider {

    @Id
    private String id = UUIDs.randomBase64UUID();

    @NotNull
    private String name;

    private List<TermsAndConditions> termsAndConditions = new ArrayList<>();

    private List<CancellationPolicy> cancellationPolicies = new ArrayList<>();

    private Status status = Status.PUBLISHED;

    public LearningProvider() {
    }

    public LearningProvider(@NotNull String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<TermsAndConditions> getTermsAndConditions() {
        return unmodifiableList(termsAndConditions);
    }

    public void setTermsAndConditions(List<TermsAndConditions> termsAndConditions) {
        this.termsAndConditions.clear();
        if (termsAndConditions != null) {
            this.termsAndConditions.addAll(termsAndConditions);
        }
    }

    public List<CancellationPolicy> getCancellationPolicies() {
        return unmodifiableList(cancellationPolicies);
    }

    public void setCancellationPolicy(List<CancellationPolicy> cancellationPolicies) {
        this.cancellationPolicies.clear();
        if (cancellationPolicies != null) {
            this.cancellationPolicies.addAll(cancellationPolicies);
        }
    }

    @Override
    public String toString() {
        return "LearningProvider{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", status=" + status +
                '}';
    }
}

