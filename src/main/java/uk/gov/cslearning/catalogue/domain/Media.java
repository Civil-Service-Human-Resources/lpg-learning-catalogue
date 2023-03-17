package uk.gov.cslearning.catalogue.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

import javax.validation.constraints.NotNull;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.data.elasticsearch.annotations.FieldType.Date;

@Document(indexName = "media")
public class Media {
    @Id
    private String id;

    @NotNull
    private String name;

    @NotNull
    private String container;

    @NotNull
    @Field(type = Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateAdded;

    @NotNull
    private String path;

    private Map<String, String> metadata = new HashMap<>();

    private long fileSizeKB;
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

    public long getFileSizeKB() {
        return fileSizeKB;
    }

    public void setFileSizeKB(long fileSizeKB) {
        this.fileSizeKB = fileSizeKB;
    }

    @JsonProperty
    public String formatFileSize() {
        long sizeBytes = fileSizeKB * 1024;
        if(sizeBytes <= 0) return "0";
        final String[] units = new String[] { "B", "kB", "MB", "GB", "TB" };
        int digitGroups = (int) (Math.log10(sizeBytes)/Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(sizeBytes/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
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

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    @JsonIgnore
    public String getMetadataWithCustomKey(CustomMediaMetadata key) {
        return this.metadata.get(key.getMetadataKey());
    }
}
