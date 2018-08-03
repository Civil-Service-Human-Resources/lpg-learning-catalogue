package uk.gov.cslearning.catalogue.domain.module;

import org.elasticsearch.common.UUIDs;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class Event {

    private String id = UUIDs.randomBase64UUID();

    @NotNull
    private String location;

    private Integer capacity;

    private Integer minCapacity;

    private String address;

    private String joiningInstructions;

    private List<Session> sessions = new ArrayList<>();

    public Event() {
    }

    public Event(@NotNull String location) {
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getMinCapacity() {
        return minCapacity;
    }

    public void setMinCapacity(Integer minCapacity) {
        this.minCapacity = minCapacity;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getJoiningInstructions() {
        return joiningInstructions;
    }

    public void setJoiningInstructions(String joiningInstructions) {
        this.joiningInstructions = joiningInstructions;
    }

    public List<Session> getSessions() {
        return unmodifiableList(sessions);
    }

    public void setSessions(List<Session> sessions) {
        this.sessions.clear();
        if (sessions != null) {
            this.sessions.addAll(sessions);
        }    }
}
