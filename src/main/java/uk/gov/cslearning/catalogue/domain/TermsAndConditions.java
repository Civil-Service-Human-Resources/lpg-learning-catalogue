package uk.gov.cslearning.catalogue.domain;

import org.elasticsearch.common.UUIDs;
import org.springframework.data.annotation.Id;

public class TermsAndConditions {
    @Id
    private String id = UUIDs.randomBase64UUID();

    private String name;

    private String content;

    public TermsAndConditions() {
    }

    public TermsAndConditions(String name, String content) {
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

    public String getId(){
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
