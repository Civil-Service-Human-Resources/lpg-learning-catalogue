package uk.gov.cslearning.catalogue.domain;

import org.apache.lucene.index.Term;
import org.elasticsearch.common.UUIDs;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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

    public List<TermsAndConditions> getTermsAndConditionsList() {
        return unmodifiableList(termsAndConditions);
    }

    public TermsAndConditions getTermsAndConditionsById(String id){
        List<TermsAndConditions> termsAndConditionsList = getTermsAndConditionsList();
        Optional<TermsAndConditions> termsAndConditions = termsAndConditionsList.stream().filter(t -> t.getId().equals(id)).findFirst();
        return termsAndConditions.get();
    }

    public void addTermsAndConditions(TermsAndConditions termsAndConditions){
        this.termsAndConditions .add(termsAndConditions);
    }

    public void removeTermsAndConditions(TermsAndConditions termsAndConditions){
        this.termsAndConditions.remove(termsAndConditions);
    }

    public List<CancellationPolicy> getCancellationPolicy() {
        return unmodifiableList(cancellationPolicies);
    }

    public CancellationPolicy getCancellationPolicyById(String id) {
        List<CancellationPolicy> policies = getCancellationPolicies();
        Optional<CancellationPolicy> policy = policies.stream().filter(p -> p.getId().equals(id)).findFirst();
        return policy.get();
    }

    public List<CancellationPolicy> getCancellationPolicies() {
        return unmodifiableList(cancellationPolicies);
    }

    public void addCancellationPolicy(CancellationPolicy cancellationPolicy) {
        this.cancellationPolicies.add(cancellationPolicy);
    }

    public void removeCancellationPolicy(CancellationPolicy cancellationPolicy) {
        this.cancellationPolicies.remove(cancellationPolicy);
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

