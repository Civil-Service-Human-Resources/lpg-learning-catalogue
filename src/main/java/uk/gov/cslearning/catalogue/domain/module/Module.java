package uk.gov.cslearning.catalogue.domain.module;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.UUID;

@Document(indexName = "lpg", type = "module")
public abstract class Module {

    @Id
    private String id;

    public Module() {
    }

    public String getId() {
        return id;
    }

    public abstract String getType();
}
