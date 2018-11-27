package uk.gov.cslearning.catalogue.service.record.model;

import java.net.URI;
import java.time.Instant;

public class Booking {
    private Integer id;

    private String learner;

    private String learnerEmail;

    private URI event;

    private String status;

    private Instant bookingTime;

    private URI paymentDetails;

    public Booking(){}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLearner() {
        return learner;
    }

    public void setLearner(String learner) {
        this.learner = learner;
    }

    public String getLearnerEmail() {
        return learnerEmail;
    }

    public void setLearnerEmail(String learnerEmail) {
        this.learnerEmail = learnerEmail;
    }

    public URI getPaymentDetails() {
        return paymentDetails;
    }

    public void setPaymentDetails(URI paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public URI getEvent() {
        return event;
    }

    public void setEvent(URI event) {
        this.event = event;
    }

    public Instant getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(Instant bookingTime) {
        this.bookingTime = bookingTime;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", learner='" + learner + '\'' +
                ", learnerEmail='" + learnerEmail + '\'' +
                ", event=" + event +
                ", status='" + status + '\'' +
                ", bookingTime=" + bookingTime +
                ", paymentDetails=" + paymentDetails +
                '}';
    }
}
