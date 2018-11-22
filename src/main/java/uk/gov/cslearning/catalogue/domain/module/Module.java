package uk.gov.cslearning.catalogue.domain.module;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.elasticsearch.common.UUIDs;
import uk.gov.cslearning.catalogue.domain.Status;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(FaceToFaceModule.class),
        @JsonSubTypes.Type(ELearningModule.class),
        @JsonSubTypes.Type(LinkModule.class),
        @JsonSubTypes.Type(VideoModule.class),
        @JsonSubTypes.Type(FileModule.class)
})
public abstract class Module {

    private String id = UUIDs.randomBase64UUID();

    @NotNull
    private String title;

    @NotNull
    private String description;

    @NotNull
    private Long duration;

    private BigDecimal cost = new BigDecimal(0);

    private boolean optional;

    private Status status;

    public Module() {
    }

    public Module(@NotNull String title, @NotNull String description, @NotNull Long duration) {
        this.title = title;
        this.description = description;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public void setCost(BigDecimal cost) {
        this.cost = cost;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getModuleType() {
        String className = this.getClass().getName();

        if (this instanceof FaceToFaceModule) {
            return "face-to-face";
        }

        if (this instanceof LinkModule) {
            return "link";
        }

        if (this instanceof VideoModule) {
            return "video";
        }

        if (this instanceof ELearningModule) {
            return "elearning";
        }

        if (this instanceof FileModule) {
            return "file";
        }

        return className;
    }
}
