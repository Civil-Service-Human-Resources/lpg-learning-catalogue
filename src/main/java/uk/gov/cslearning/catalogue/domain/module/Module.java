package uk.gov.cslearning.catalogue.domain.module;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import org.elasticsearch.common.UUIDs;
import org.springframework.data.elasticsearch.annotations.Field;
import uk.gov.cslearning.catalogue.domain.Status;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.data.elasticsearch.annotations.FieldType.Date;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(FaceToFaceModule.class),
        @JsonSubTypes.Type(ELearningModule.class),
        @JsonSubTypes.Type(LinkModule.class),
        @JsonSubTypes.Type(VideoModule.class),
        @JsonSubTypes.Type(FileModule.class)
})
@Data
public abstract class Module {

    protected String id = UUIDs.randomBase64UUID();

    @NotNull
    protected String title;

    @NotNull
    protected String description;

    @NotNull
    protected Long duration;

    protected BigDecimal cost = new BigDecimal(0);

    protected boolean optional;

    protected Status status;

    protected boolean associatedLearning;

    @JsonIgnore
    protected String type;

    @Field(type = Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    protected LocalDateTime createdTimestamp;

    @Field(type = Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    protected LocalDateTime updatedTimestamp;

    public Module() {
    }

    public Module(@NotNull String title,
                  @NotNull String description,
                  @NotNull Long duration,
                  @NotNull String type) {
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.type = type;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public boolean isAssociatedLearning() {
        return associatedLearning;
    }

    public void setAssociatedLearning(boolean associatedLearning) {
        this.associatedLearning = associatedLearning;
    }

    public String getModuleType(){
        return this.type;
    }

}
