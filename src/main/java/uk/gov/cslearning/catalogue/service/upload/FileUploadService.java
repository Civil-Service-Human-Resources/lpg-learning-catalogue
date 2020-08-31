package uk.gov.cslearning.catalogue.service.upload;

import java.io.IOException;

import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.Upload;

public interface FileUploadService {
    Upload upload(FileUpload fileUpload);

    Upload uploadImageForSkills(FileUpload fileUpload) throws IOException;

    void delete(String filePath);

    void deleteDirectory(String filePath);
}
