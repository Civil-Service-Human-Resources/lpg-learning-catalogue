package uk.gov.cslearning.catalogue.domain.module;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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

    private boolean associatedLearning;

    private String type;

    @Field(type = Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTimestamp;

    @Field(type = Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedTimestamp;

    public Module() {
    }

    public Module(String type){
        this.type = type;
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

    public boolean isAssociatedLearning() {
        return associatedLearning;
    }

    public void setAssociatedLearning(boolean associatedLearning) {
        this.associatedLearning = associatedLearning;
    }

    public LocalDateTime getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(LocalDateTime createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public LocalDateTime getUpdatedTimestamp() {
        return updatedTimestamp;
    }

    public void setUpdatedTimestamp(LocalDateTime updatedTimestamp) {
        this.updatedTimestamp = updatedTimestamp;
    }

//    public abstract String getModuleType();

    public String getType(){
        return this.type;
    }

    public void setType(String type){
        this.type = type;
    }

}
