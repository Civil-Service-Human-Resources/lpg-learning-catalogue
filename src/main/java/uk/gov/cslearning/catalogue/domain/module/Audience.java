package uk.gov.cslearning.catalogue.domain.module;

import lombok.Data;
import org.elasticsearch.common.UUIDs;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
public class Audience {
    public enum Type {
        OPEN,
        CLOSED_COURSE,
        PRIVATE_COURSE,
        REQUIRED_LEARNING
    }

    private String id = UUIDs.randomBase64UUID();

    private String name;

    private Set<String> areasOfWork = new HashSet<>();

    private Set<String> departments = new HashSet<>();

    private Set<String> grades = new HashSet<>();

    private Set<String> interests = new HashSet<>();

    private Instant requiredBy;

    private String frequency;

    private Type type;

    private String eventId;

    public Audience() {
    }

}
