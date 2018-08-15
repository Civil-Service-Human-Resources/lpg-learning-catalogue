package uk.gov.cslearning.catalogue.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import uk.gov.cslearning.catalogue.domain.module.Module;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Document(indexName = "lpg-resources", type = "resources")
public class Resource {

    @Id
    private String id;

    @Field(type = FieldType.keyword)
    private String courseId;

    private String title;

    private String type;

    private BigDecimal price;

    private String shortDescription;

    private String description;

    private String learningOutcomes;

    private List<Module> modules = new ArrayList<>();

    private Course course;

    public Resource() {
    }

    public Resource(String id, String courseId, String type, String title, BigDecimal price, String shortDescription, String description, String learningOutcomes) {
        this.id = id;
        this.courseId = courseId;
        this.type = type;
        this.title = title;
        this.price = price;
        this.shortDescription = shortDescription;
        this.description = description;
        this.learningOutcomes = learningOutcomes;
    }


    public static ArrayList<Resource> fromCourse(Course course) {
        ArrayList<Resource> out = new ArrayList<Resource>();

        Resource newResource = new Resource(
                course.getId(),
                "0", // for sorting
                "course",
                course.getTitle(),
                null,
                course.getShortDescription(),
                course.getDescription(),
                course.getLearningOutcomes()
        );

        // modules needed for various metrics. Potential to move this into
        // java layer

        List<Module> modules = course.getModules();
        newResource.setModules(modules);

        out.add(newResource);

        if (!modules.isEmpty()) {
            // now lets iterate through any modules
            for (Module module : modules) {
                newResource = new Resource(
                        module.getId(),
                        course.getId(),
                        module.getModuleType(),
                        module.getTitle(),
                        module.getCost(),
                        null,
                        module.getDescription(),
                        null
                );
                newResource.setCourse(course);
                out.add(newResource);
            }
        }

        return out;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("type", type)
                .append("title", title)
                .toString();
    }

    public List<Module> getModules() {
        return modules;
    }

    public void setModules(List<Module> modules) {
        this.modules = modules;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
 
