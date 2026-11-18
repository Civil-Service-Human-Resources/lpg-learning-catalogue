package uk.gov.cslearning.catalogue.domain;

import lombok.Getter;

@Getter
public enum CourseLevel {
    AWARENESS("Awareness"),
    WORKING("Working"),
    PRACTITIONER("Practitioner"),
    EXPERT("Expert"),
    ;

    private final String name;

    CourseLevel(String name) {
        this.name = name;
    }
}
