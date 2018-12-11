package uk.gov.cslearning.catalogue.dto;

import lombok.Data;

@Data
public class EventDto {
    private String id;
    private ModuleDto module;
    private CourseDto course;
}
