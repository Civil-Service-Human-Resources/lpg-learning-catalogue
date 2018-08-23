package uk.gov.cslearning.catalogue.domain.media;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.io.FileUtils;
import org.elasticsearch.common.UUIDs;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Document(indexName = "media", type = "media")
public class MediaEntity implements Media {
    @Id
    private String id = UUIDs.randomBase64UUID();

    @NotNull
    private String name;

    @NotNull
    private String container;

    @NotNull
    private LocalDateTime dateAdded;

    @NotNull
    private String path;

    private long fileSize;
    private String extension;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    @JsonProperty
    public String formatFileSize() {
        return FileUtils.byteCountToDisplaySize(fileSize * 1024);
    }

    public String getContainer() {
        return container;
    }

    public void setContainer(String container) {
        this.container = container;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUid() {
        return id;
    }
}
