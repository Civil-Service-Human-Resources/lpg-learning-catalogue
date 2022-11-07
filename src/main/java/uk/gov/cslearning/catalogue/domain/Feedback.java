package uk.gov.cslearning.catalogue.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.elasticsearch.common.UUIDs;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import static com.google.common.base.Preconditions.checkArgument;

@Document(indexName = "lpg-feedback2")
public class Feedback {

    @Id
    private String id = UUIDs.randomBase64UUID();

    private Integer content;

    private Integer presentation;

    private Integer relevance;

    private Integer interactivity;

    private String comments;

    private String courseId;

    private String moduleId;

    private String userId;

    @JsonCreator
    public Feedback(@JsonProperty("courseId") String courseId, @JsonProperty("moduleId") String moduleId,
                    @JsonProperty("userId") String userId) {
        checkArgument(courseId != null);
        checkArgument(moduleId != null);
        checkArgument(userId != null);
        this.courseId = courseId;
        this.moduleId = moduleId;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getContent() {
        return content;
    }

    public void setContent(Integer content) {
        this.content = content;
    }

    public Integer getPresentation() {
        return presentation;
    }

    public void setPresentation(Integer presentation) {
        this.presentation = presentation;
    }

    public Integer getRelevance() {
        return relevance;
    }

    public void setRelevance(Integer relevance) {
        this.relevance = relevance;
    }

    public Integer getInteractivity() {
        return interactivity;
    }

    public void setInteractivity(Integer interactivity) {
        this.interactivity = interactivity;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getModuleId() {
        return moduleId;
    }

    public String getUserId() {
        return userId;
    }
}