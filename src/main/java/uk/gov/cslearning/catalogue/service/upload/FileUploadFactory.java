package uk.gov.cslearning.catalogue.service.upload;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.elasticsearch.common.UUIDs;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.cslearning.catalogue.dto.FileUpload;

@Component
public class FileUploadFactory {
    public FileUpload create(MultipartFile file, String container, String filename) {
        return new FileUpload() {

            private final String id = UUIDs.randomBase64UUID();
            private final String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            private final String name = (null != filename) ? filename : file.getOriginalFilename();
            private long sizeKB = file.getSize() / 1024;

            @Override
            public String getId() {
                return id;
            }

            @Override
            public String getContainer() {
                return container;
            }

            @Override
            public String getExtension() {
                return extension;
            }

            @Override
            public MultipartFile getFile() {
                return file;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public long getSizeKB() {
                return sizeKB;
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
                        .toString();
            }
        };
    }
}
