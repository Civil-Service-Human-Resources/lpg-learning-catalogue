package uk.gov.cslearning.catalogue.domain.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.net.URL;

@JsonTypeName("link")
public class LinkModule extends Module {

    private URL location;
    private Long fileSize;

    @JsonCreator
    public LinkModule(@JsonProperty("location") URL location, @JsonProperty("fileSize") Long fileSize) {
        this.location = location;
        this.fileSize = fileSize;
    }

    public URL getLocation() {
        return location;
    }
    public Long getFileSize() {
        return fileSize;
    }

    public void setLocation(URL location) {
        this.location = location;
    }
    public void setInteger(Long fileSize) {
        this.fileSize = fileSize;
    }
}
