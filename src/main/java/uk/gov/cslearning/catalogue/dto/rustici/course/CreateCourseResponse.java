package uk.gov.cslearning.catalogue.dto.rustici.course;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CreateCourseResponse {
    private String webPathToCourse;
    private List<String> parserWarnings;
    private Course course;
}
