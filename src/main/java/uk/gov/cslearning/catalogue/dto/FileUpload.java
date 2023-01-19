package uk.gov.cslearning.catalogue.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.io.FilenameUtils;
import org.elasticsearch.common.UUIDs;
import org.springframework.web.multipart.MultipartFile;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Container DTO for file uploads
 */
@Data
@AllArgsConstructor
@ToString
public class FileUpload {
    /**
     * ModuleID
     */
    String id;
    /**
     * CourseID
     */
    String container;
    String extension;
    MultipartFile file;
    String name;
    long sizeKB;
    LocalDateTime timestamp;

    public static FileUpload createFromMetadata(MultipartFile file, String container, String filename) {
        String id = UUIDs.randomBase64UUID();
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String name = (null != filename) ? filename : file.getOriginalFilename();
        long sizeKB = file.getSize() / 1024;
        LocalDateTime timestamp = LocalDateTime.now(Clock.systemUTC());
        return new FileUpload(id, container, extension, file, name, sizeKB, timestamp);
    }

    public String getDestination() {
        return String.join("/", this.getContainer(), this.getId());
    }

}
