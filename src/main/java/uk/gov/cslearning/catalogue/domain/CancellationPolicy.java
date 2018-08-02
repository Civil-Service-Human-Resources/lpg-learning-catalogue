package uk.gov.cslearning.catalogue.domain;

import org.elasticsearch.common.UUIDs;
import org.springframework.data.annotation.Id;

public class CancellationPolicy {
    @Id
    private String id = UUIDs.randomBase64UUID();
    private String name;
    private String shortVersion;
    private String fullVersion;
    private Long dateCreated;
    private String createdByEmail;

    public CancellationPolicy(String name, String shortVersion, String fullVersion, String createdByEmail) {
        this.name = name;
        this.shortVersion = shortVersion;
        this.fullVersion = fullVersion;
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
}
