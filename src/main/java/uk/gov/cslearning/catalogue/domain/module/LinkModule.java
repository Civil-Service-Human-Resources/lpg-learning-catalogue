package uk.gov.cslearning.catalogue.domain.module;

import java.net.URL;

public class LinkModule extends Module {

    private static final String TYPE = "link";

    private URL location;

    public LinkModule(URL location) {
        this.location = location;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    public URL getLocation() {
        return location;
    }

    public void setLocation(URL location) {
        this.location = location;
    }
}
