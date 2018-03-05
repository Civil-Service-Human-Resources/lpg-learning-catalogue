package uk.gov.cslearning.catalogue.domain.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.net.URL;

@JsonTypeName("video")
public class VideoModule extends Module {

    private URL location;

    @JsonCreator
    public VideoModule(@JsonProperty("location") URL location) {
        this.location = location;
    }

    public URL getLocation() {
        return location;
    }

    public void setLocation(URL location) {
        this.location = location;
    }
}
