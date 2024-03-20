package uk.gov.cslearning.catalogue.domain.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import static java.util.Collections.unmodifiableCollection;

@JsonTypeName("face-to-face")
public class FaceToFaceModule extends Module {

    private String productCode;

    private Collection<Event> events;

    @JsonCreator
    public FaceToFaceModule(@JsonProperty("productCode") String productCode) {
        super("face-to-face");
        this.productCode = productCode;
        this.events = new HashSet<>();
    }

    public Event getEventById(String eventId){
        Collection<Event> events = getEvents();
        Optional<Event> event = events.stream().filter(e -> e.getId().equals(eventId)).findFirst();
        return event.get();
    }

    public Collection<Event> getEvents() {
        return unmodifiableCollection(events);
    }

    public void setEvents(Collection<Event> events) {
        this.events.clear();
        if (events != null) {
            this.events.addAll(events);
        }
    }

    public void removeEvent(Event event){
        events.remove(event);
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

//    public String getModuleType() {
//        return "face-to-face";
//    }
}
