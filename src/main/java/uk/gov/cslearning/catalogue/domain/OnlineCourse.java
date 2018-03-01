package uk.gov.cslearning.catalogue.domain;

import java.util.Set;

public class OnlineCourse extends Course {

    private static final String TYPE = "elearning";

    public OnlineCourse(String title, String shortDescription, String description, String learningOutcomes, Integer duration, Set<String> tags) {
        super(title, shortDescription, description, learningOutcomes, duration, tags);
    }

    @Override
    public String getType() {
        return TYPE;
    }
}
