package uk.gov.cslearning.catalogue.domain.module;

import org.elasticsearch.common.UUIDs;

import java.time.Instant;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;

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

    private Period frequency;

    private Type type;

    private String eventId;

    public Audience() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getAreasOfWork() {
        return areasOfWork;
    }

    public void setAreasOfWork(Set<String> areasOfWork) {
        this.areasOfWork = areasOfWork;
    }

    public Set<String> getDepartments() {
        return departments;
    }

    public void setDepartments(Set<String> departments) {
        this.departments = departments;
    }

    public Set<String> getGrades() {
        return grades;
    }

    public void setGrades(Set<String> grades) {
        this.grades = grades;
    }

    public Set<String> getInterests() {
        return interests;
    }

    public void setInterests(Set<String> interests) {
        this.interests = interests;
    }

    public Instant getRequiredBy() {
        return requiredBy;
    }

    public void setRequiredBy(Instant requiredBy) {
        this.requiredBy = requiredBy;
    }

    public Period getFrequency() {
        return frequency;
    }

    public void setFrequency(Period frequency) {
        this.frequency = frequency;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
