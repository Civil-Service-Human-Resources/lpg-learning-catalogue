package uk.gov.cslearning.catalogue.domain.module;

import com.fasterxml.jackson.annotation.JsonTypeName;

import java.net.URL;

@JsonTypeName("link")
public class LinkModule extends Module {

    private URL location;

    public LinkModule(URL location) {
        this.location = location;
    }

    public URL getLocation() {
        return location;
    }

    public void setLocation(URL location) {
        this.location = location;
    }
}
