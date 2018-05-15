package uk.gov.cslearning.catalogue.domain.module;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonValue;
import org.elasticsearch.common.UUIDs;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashSet;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.unmodifiableCollection;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(FaceToFaceModule.class),
        @JsonSubTypes.Type(ELearningModule.class),
        @JsonSubTypes.Type(LinkModule.class),
        @JsonSubTypes.Type(VideoModule.class),
        @JsonSubTypes.Type(DocumentModule.class)
})
public abstract class Module {

    private String id = UUIDs.randomBase64UUID();

    private String title;

    private String description;

    private Long duration;

    private BigDecimal price;

    private Collection<Audience> audiences;

    public Module() {
        audiences = new HashSet<>();
    }

    public Collection<Audience> getAudiences() {
        return unmodifiableCollection(audiences);
    }
    public void setAudiences(Collection<Audience> audiences) {
        this.audiences.clear();
        if (audiences != null) {
            this.audiences.addAll(audiences);
        }
    }

    public void addAudience(Audience audience) {
        checkArgument(audience != null);
        this.audiences.add(audience);
    }

    public String getId() {
        return id;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
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

        if (this instanceof DocumentModule) {
            return "document";
        }

        return className;
    }

}
