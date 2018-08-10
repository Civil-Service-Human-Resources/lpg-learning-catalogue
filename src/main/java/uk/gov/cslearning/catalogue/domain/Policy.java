package uk.gov.cslearning.catalogue.domain;

import org.elasticsearch.common.UUIDs;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;

public class Policy {

    @Id
    private String id = UUIDs.randomBase64UUID();

    @NotNull
    private String name;

    @NotNull
    private String content;

    private LearningProvider learningProvider;

    public Policy() {
    }

    public Policy(@NotNull String name, @NotNull String content) {
        this.name = name;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContent() {
        return content;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Policy{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
