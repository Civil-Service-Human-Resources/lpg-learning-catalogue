package uk.gov.cslearning.catalogue.dto;

import lombok.Data;

@Data
public class ModuleDto {
    private String id;
    private String title;
    private String type;
    private boolean required;
    private CourseDto course;
}
