package uk.gov.cslearning.catalogue.domain.module;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.net.URL;

@JsonTypeName("video")
public class VideoModule extends Module {

    private URL location;

    public VideoModule(URL location) {
        this.location = location;
    }

    public URL getLocation() {
        return location;
    }

    public void setLocation(URL location) {
        this.location = location;
    }
}
