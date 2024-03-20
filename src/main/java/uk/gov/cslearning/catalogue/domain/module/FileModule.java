package uk.gov.cslearning.catalogue.domain.module;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("file")
public class FileModule extends Module {

    private String url;
    private Long fileSize;
    private String mediaId;

    @JsonCreator
    public FileModule(@JsonProperty("url") String url, @JsonProperty("fileSize") Long fileSize) {
        super("file");
        this.url = url;
        this.fileSize = fileSize;
    }

    public String getUrl() {
        return url;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

//    public String getModuleType() {
//        return "file";
//    }
}
