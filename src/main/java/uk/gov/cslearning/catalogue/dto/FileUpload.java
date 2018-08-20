package uk.gov.cslearning.catalogue.dto;

import org.springframework.web.multipart.MultipartFile;

public interface FileUpload {
    String getContainer();

    String getExtension();

    MultipartFile getFile();

    String getName();

    long getSize();
}
