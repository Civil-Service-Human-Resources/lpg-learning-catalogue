package uk.gov.cslearning.catalogue.domain.module;

import java.net.URL;

public class VideoModule extends Module {

    private static final String TYPE = "video";

    private URL location;

    public VideoModule(URL location) {
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
