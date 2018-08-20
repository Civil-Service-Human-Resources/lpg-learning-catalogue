package uk.gov.cslearning.catalogue.domain.media;

import org.apache.commons.io.FileUtils;

import java.time.LocalDateTime;

@org.springframework.data.elasticsearch.annotations.Document(indexName = "media", type = "media")
public class Document implements Media {
    private String id;
    private String name;
    private long fileSize;
    private String container;
    private LocalDateTime dateAdded;
    private String extension;
    private String path;
    private String uid;

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
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
