package uk.gov.cslearning.catalogue.dto.factory;

import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.Media;

import java.time.LocalDateTime;

@Component
public class MediaFactory {

    public Media create(FileUpload fileUpload) {
        return new Media() {
            @Override
            public String formatFileSize() {
                return FileUtils.byteCountToDisplaySize(fileUpload.getSize() * 1024);
            }

            @Override
            public String getContainer() {
                return fileUpload.getContainer();
            }

            @Override
            public LocalDateTime getDateAdded() {
                return LocalDateTime.now();
            }

            @Override
            public String getExtension() {
                return fileUpload.getExtension();
            }

            @Override
            public long getId() {
                return 0;
            }

            @Override
            public String getName() {
                return fileUpload.getName();
            }

            @Override
            public String getPath() {
                return null;
            }

            @Override
            public String getUid() {
                return null;
            }
        };
    }
}
