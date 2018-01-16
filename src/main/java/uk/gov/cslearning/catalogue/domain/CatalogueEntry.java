package uk.gov.cslearning.catalogue.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class CatalogueEntry {

    private String title;

    private URL location;

    private Set<String> tag;

    @JsonCreator
    public CatalogueEntry(@JsonProperty("title") String title, @JsonProperty("location") URL location,
                          @JsonProperty("tag") Set<String> tag) {
        this.title = title;
        this.location = location;
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public URL getLocation() {
        return location;
    }

    public Set<String> getTag() {
        return tag;
    }
}
