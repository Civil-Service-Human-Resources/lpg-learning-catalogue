package uk.gov.cslearning.catalogue.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URL;

public class CatalogueEntry {

    private String title;

    private URL location;

    @JsonCreator
    public CatalogueEntry(@JsonProperty("title") String title, @JsonProperty("location") URL location) {
        this.title = title;
        this.location = location;
    }

    public String getTitle() {
        return title;
    }

    public URL getLocation() {
        return location;
    }
}
