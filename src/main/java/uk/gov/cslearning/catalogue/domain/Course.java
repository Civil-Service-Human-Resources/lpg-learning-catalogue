package uk.gov.cslearning.catalogue.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.elasticsearch.common.UUIDs;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import uk.gov.cslearning.catalogue.domain.Owner.Owner;
import uk.gov.cslearning.catalogue.domain.module.Audience;
import uk.gov.cslearning.catalogue.domain.module.Module;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;
import static org.springframework.data.elasticsearch.annotations.FieldType.Date;

@Document(indexName = "courses")
public class Course {

    @Id
    private String id = UUIDs.randomBase64UUID();

    @NotNull
    private String title;

    @NotNull
    @Size(max = 160)
    private String shortDescription;

    @NotNull
    @Size(max = 1500)
    private String description;

    private String learningOutcomes;

    private List<Module> modules = new ArrayList<>();

    private LearningProvider learningProvider;

    @Field(type = FieldType.Nested)
    private Set<Audience> audiences = new HashSet<>();

    private String preparation;

    private Owner owner;

    @NotNull
    private Visibility visibility = Visibility.PUBLIC;

    private Status status = Status.DRAFT;

    private String topicId;

    @Field(type = Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdTimestamp;

    @Field(type = Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedTimestamp;

    private BigDecimal cost = new BigDecimal(0);

    public Course() {
    }

    public Course(@NotNull String title, @NotNull String shortDescription, @NotNull String description, @NotNull Visibility visibility) {
        this.title = title;
        this.shortDescription = shortDescription;
        this.description = description;
        this.visibility = visibility;
    }

    @JsonIgnore
    public List<String> getMandatoryDepartmentCodes() {
        return getRequiredAudiences()
                .stream().flatMap(audience -> audience.getDepartments().stream()).collect(Collectors.toList());
    }

    @JsonIgnore
    public List<Audience> getRequiredAudiences() {
        return getAudiences().stream().filter(Audience::isRequired).collect(Collectors.toList());
    }

    @JsonIgnore
    public List<Audience> getMandatoryAudiencesForDepartments(List<String> departmentCodes) {
        return getAudiences()
                .stream()
                .filter(audience -> audience.isRequiredForDepartments(departmentCodes))
                .collect(Collectors.toList());
    }

    @JsonIgnore
    public void upsertModule(Module newModule) {
        List<Module> mods = new ArrayList<>(getModules());
        int indexToReplace = -1;
        for (int i = 0; i < mods.size(); i++) {
            if (newModule.getId().equals(mods.get(i).getId())) {
                indexToReplace = i;
                break;
            }
        }
        newModule.setUpdatedTimestamp(LocalDateTime.now(Clock.systemUTC()));
        if (indexToReplace > -1) {
            mods.set(indexToReplace, newModule);
        } else {
            newModule.setCreatedTimestamp(LocalDateTime.now(Clock.systemUTC()));
            mods.add(newModule);
        }
        setModules(mods);
    }

    @JsonIgnore
    public void setCostFromModules() {
        setCost(BigDecimal.valueOf(modules.stream().mapToDouble(m -> m.getCost().doubleValue()).sum()));
    }

    public List<Module> getModules() {
        return unmodifiableList(modules);
    }

    @JsonIgnore
    public Optional<Module> getModuleById(String moduleId) {
        return getModules().stream().filter(m -> m.getId().equals(moduleId)).findFirst();
    }

    public void setModules(List<Module> modules) {
        this.modules.clear();
        if (modules != null) {
            this.modules.addAll(modules);
        }
    }

    public void deleteModule(Module module) {
        for (Iterator<Module> it = modules.iterator(); it.hasNext(); ) {
            Module m = it.next();
            if (m.getId().equals(module.getId())) {
                it.remove();
            }
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

    public void setLearningProvider(LearningProvider learningProvider) {
        this.learningProvider = learningProvider;
    }

    public Set<Audience> getAudiences() {
        return unmodifiableSet(audiences);
    }

    public void setAudiences(Set<Audience> audiences) {
        this.audiences.clear();
        if (audiences != null) {
            this.audiences.addAll(audiences);
        }
    }

    public void deleteAudience(Audience audience) {
        audiences.remove(audience);
    }

    public String getPreparation() {
        return preparation;
    }

    public void setPreparation(String preparation) {
        this.preparation = preparation;
    }

    public Visibility getVisibility() {
        return visibility;
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LearningProvider getLearningProvider() {
        return learningProvider;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
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

    public BigDecimal getCost() { return cost; }

    public void setCost(BigDecimal cost) { this.cost = cost; }

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
