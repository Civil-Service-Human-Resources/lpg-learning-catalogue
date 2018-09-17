package uk.gov.cslearning.catalogue.domain.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("file")
public class FileModule extends Module {

    private String url;
    private Long fileSize;

    @JsonCreator
    public FileModule(@JsonProperty("url") String url, @JsonProperty("fileSize") Long fileSize) {
        this.url = url;
        this.fileSize = fileSize;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
}
