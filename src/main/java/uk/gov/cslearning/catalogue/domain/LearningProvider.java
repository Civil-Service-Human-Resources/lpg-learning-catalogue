package uk.gov.cslearning.catalogue.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.elasticsearch.common.UUIDs;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

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

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void addCancellationPolicy(CancellationPolicy cancellationPolicy) {
        this.cancellationPolicies.add(cancellationPolicy);
    }

    public List<CancellationPolicy> getCancellationPolicies() {
        return cancellationPolicies;
    }

    public void addTermsAndConditions(TermsAndConditions termsAndConditions) {
        this.termsAndConditions.add(termsAndConditions);
    }

    public List<TermsAndConditions> getTermsAndConditions() {
        return termsAndConditions;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id,", id)
                .append("name", name)
                .toString();
    }
}

