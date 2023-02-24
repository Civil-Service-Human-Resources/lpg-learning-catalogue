package uk.gov.cslearning.catalogue.dto.rustici.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Course {
    private String id;
    private String title;
    private String xapiActivityId;
    private Date updated;
    private int version;
    private String activityId;
    private String courseLearningStandard;
    private boolean launchable;
    private Metadata metadata;
}
