package uk.gov.cslearning.catalogue.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.elasticsearch.common.UUIDs;
import org.springframework.web.multipart.MultipartFile;

import java.time.Clock;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FileUpload {
    String id;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("container", container)
                .append("file", file)
                .append("extension", extension)
                .append("name", name)
                .append("sizeKB", sizeKB)
                .append("timestamp", timestamp)
                .toString();
    }
}
