package uk.gov.cslearning.catalogue.service;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.cslearning.catalogue.dto.FileUpload;

@Component
public class FileUploadFactory {
    public FileUpload create(MultipartFile file, String container, String filename) {
        return new FileUpload() {
            @Override
            public String getContainer() {
                return container;
            }

            @Override
            public String getExtension() {
                return FilenameUtils.getExtension(getName());
            }

            @Override
            public MultipartFile getFile() {
                return file;
            }

            @Override
            public String getName() {
                return (null != filename) ? filename : file.getOriginalFilename();
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
