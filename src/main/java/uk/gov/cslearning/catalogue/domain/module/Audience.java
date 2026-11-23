package uk.gov.cslearning.catalogue.domain.module;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.elasticsearch.annotations.Field;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import static org.springframework.data.elasticsearch.annotations.FieldType.Date;

@Data
public class Audience {
    public enum Type {
        OPEN,
        CLOSED_COURSE,
        PRIVATE_COURSE,
        REQUIRED_LEARNING
    }

    private String id = RandomStringUtils.secure().nextAlphanumeric(22);

    private String name;

    private Set<String> areasOfWork = new HashSet<>();

    private Set<String> departments = new HashSet<>();

    private Set<String> grades = new HashSet<>();

    private Set<String> interests = new HashSet<>();

    @Field(type = Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private Instant requiredBy;

    private String frequency;

    private Type type;

    private String eventId;

    public Audience() {
    }

    @JsonIgnore
    public boolean isRequired() {
        return type != null && type.equals(Type.REQUIRED_LEARNING) && requiredBy != null;
    }

}
