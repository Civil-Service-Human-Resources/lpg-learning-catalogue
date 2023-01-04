package uk.gov.cslearning.catalogue.service.record.model;

import lombok.Data;

@Data
public class Event {
    private Integer id;

    private String uid;

    private String path;

    private String status;

    private String cancellationReason;

    private Integer activeBookingCount;

    public Event() {}

    public Integer getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }
}

