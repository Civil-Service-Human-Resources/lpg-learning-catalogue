package uk.gov.cslearning.catalogue.domain.module;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
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

    public Module(@NotNull String title,
                  @NotNull String description,
                  @NotNull Long duration,
                  @NotNull String type) {
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.type = type;
    }

}
