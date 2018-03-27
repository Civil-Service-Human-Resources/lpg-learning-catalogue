package uk.gov.cslearning.catalogue.domain.module;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
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
        @JsonSubTypes.Type(VideoModule.class)
})
public abstract class Module {

    private String id = UUIDs.randomBase64UUID();

    private String title;

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
}
