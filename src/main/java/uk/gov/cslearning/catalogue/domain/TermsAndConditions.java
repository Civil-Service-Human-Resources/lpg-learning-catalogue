package uk.gov.cslearning.catalogue.domain;

import org.elasticsearch.common.UUIDs;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;

public class TermsAndConditions {
    @Id
    private String id = UUIDs.randomBase64UUID();

    @NotNull
    private String name;

    @NotNull
    private String content;

    public TermsAndConditions() {
    }

    public TermsAndConditions(@NotNull String name, @NotNull String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "TermsAndConditions{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
