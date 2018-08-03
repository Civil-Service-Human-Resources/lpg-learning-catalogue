package uk.gov.cslearning.catalogue.domain;

import org.elasticsearch.common.UUIDs;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.validation.constraints.NotNull;

@Document(indexName = "lpg-cancellation-policies", type = "cancellationPolicy")
public class CancellationPolicy {
    @Id
    private String id = UUIDs.randomBase64UUID();

    @NotNull
    private String name;

    @NotNull
    private String shortVersion;

    @NotNull
    private String fullVersion;

    public CancellationPolicy() {
    }

    public CancellationPolicy(@NotNull String name, @NotNull String shortVersion, @NotNull String fullVersion) {
        this.name = name;
        this.shortVersion = shortVersion;
        this.fullVersion = fullVersion;
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

    public String getShortVersion() {
        return shortVersion;
    }

    public void setShortVersion(String shortVersion) {
        this.shortVersion = shortVersion;
    }

    public String getFullVersion() {
        return fullVersion;
    }

    public void setFullVersion(String fullVersion) {
        this.fullVersion = fullVersion;
    }
}
