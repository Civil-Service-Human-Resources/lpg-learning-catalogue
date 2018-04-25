package uk.gov.cslearning.catalogue.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import uk.gov.cslearning.catalogue.domain.module.Module;
import java.util.ArrayList;
import java.util.List;


@Document(indexName = "lpg-resources", type = "resources")
public class Resource {

    @Id
    private String id;

    @Field(type = FieldType.keyword)
    private String courseId;

    private String title;

    private String shortDescription;

    private String description;

    private String learningOutcomes;

    private List<Module> modules = new ArrayList<>();


    public Resource() {
    }

    public Resource(String id,String courseId, String title, String shortDescription, String description, String learningOutcomes) {
        this.id = id;
        this.courseId = courseId;
        this.title = title;
        this.shortDescription = shortDescription;
        this.description = description;
        this.learningOutcomes = learningOutcomes;

        System.out.println("creating "+id+" "+
                courseId+" "+
                title+" "+
                shortDescription+" "+
                description+" "+
                learningOutcomes);

    }


    public  static ArrayList<Resource> fromCourse(Course course) {
        ArrayList<Resource> out = new ArrayList<Resource>();

        Resource newResource = new Resource(
                course.getId(),
                "0", // for sorting
                course.getTitle(),
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
            for ( Module module: modules) {
                newResource = new Resource(
                        module.getId(),
                        course.getId(),
                        module.getTitle(),
                        null,
                        module.getDescription(),
                        null
                );
                out.add(newResource);
            }
        }

        return out;
    };

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
                .append("title", title)
                .toString();
    }

    public List <Module> getModules() {
        return modules;
    }

    public void setModules(List <Module> modules) {
        this.modules = modules;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
}
 
