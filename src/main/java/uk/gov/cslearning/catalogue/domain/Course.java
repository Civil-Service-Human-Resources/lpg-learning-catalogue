package uk.gov.cslearning.catalogue.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.elasticsearch.common.UUIDs;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import uk.gov.cslearning.catalogue.domain.module.Module;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Collections.unmodifiableList;

@Document(indexName = "lpg", type = "course")
public class Course {

    @Id
    private String id = UUIDs.randomBase64UUID();

    private String title;

    private String shortDescription;

    private String description;

    private String learningOutcomes;

    private Long duration;

    private Set<String> tags = new HashSet<>();

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime requiredBy;

    private Frequency frequency;

    @Field(type = FieldType.Nested)
    private List<Module> modules = new ArrayList<>();

    public Course() {
    }

    public Course(String title, String shortDescription, String description, String learningOutcomes, Long duration, Set<String> tags) {
        this.title = title;
        this.shortDescription = shortDescription;
        this.description = description;
        this.learningOutcomes = learningOutcomes;
        this.duration = duration;
        setTags(tags);
    }

    public List<Module> getModules() {
        return unmodifiableList(modules);
    }

    public void setModules(List<Module> modules) {
        this.modules.clear();
        if (modules != null) {
            this.modules.addAll(modules);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLearningOutcomes() {
        return learningOutcomes;
    }

    public void setLearningOutcomes(String learningOutcomes) {
        this.learningOutcomes = learningOutcomes;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags.clear();
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    public LocalDateTime getRequiredBy() {
        return requiredBy;
    }

    public void setRequiredBy(LocalDateTime requiredBy) {
        this.requiredBy = requiredBy;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    @Override
    public boolean equals(Object object) {
        if (object == this) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (!(object instanceof Course)) {
            return false;
        }
        Course rhs = (Course) object;
        return this.id.equals(rhs.id);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("title", title)
                .toString();
    }
}
