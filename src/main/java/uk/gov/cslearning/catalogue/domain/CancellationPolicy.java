package uk.gov.cslearning.catalogue.domain;

import org.elasticsearch.common.UUIDs;
import org.springframework.data.annotation.Id;

public class CancellationPolicy {

    @Id
    private String id = UUIDs.randomBase64UUID();

    private String name;

    private String shortVersion;

    private String fullVersion;

    public CancellationPolicy() {
    }

    public CancellationPolicy(String name, String shortVersion, String fullVersion) {
        this.name = name;
        this.shortVersion = shortVersion;
        this.fullVersion = fullVersion;
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

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "CancellationPolicy{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
