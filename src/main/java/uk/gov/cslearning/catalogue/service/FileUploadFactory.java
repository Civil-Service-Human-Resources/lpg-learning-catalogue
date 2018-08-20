package uk.gov.cslearning.catalogue.service;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.cslearning.catalogue.dto.FileUpload;

@Component
public class FileUploadFactory {
    public FileUpload create(MultipartFile file, String container) {
        return new FileUpload() {
            @Override
            public String getContainer() {
                return container;
            }

            @Override
            public String getExtension() {
                return FilenameUtils.getExtension(file.getName());
            }

            @Override
            public MultipartFile getFile() {
                return file;
            }

            @Override
            public String getName() {
                return file.getName();
            }

            /**
             * File size in KB
             **/
            @Override
            public long getSize() {
                return file.getSize() / 1024;
            }
        };
    }
}
