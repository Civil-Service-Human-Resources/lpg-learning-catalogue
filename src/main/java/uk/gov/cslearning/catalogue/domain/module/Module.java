package uk.gov.cslearning.catalogue.domain.module;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.elasticsearch.common.UUIDs;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(ClassroomModule.class),
        @JsonSubTypes.Type(ELearningModule.class),
        @JsonSubTypes.Type(LinkModule.class),
        @JsonSubTypes.Type(VideoModule.class)
})
public abstract class Module {

    private String id = UUIDs.randomBase64UUID();

    public Module() {
    }

    public String getId() {
        return id;
    }
}
