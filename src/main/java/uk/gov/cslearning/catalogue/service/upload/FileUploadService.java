package uk.gov.cslearning.catalogue.service.upload;

import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.Upload;

public interface FileUploadService {
    Upload upload(FileUpload fileUpload);
}
