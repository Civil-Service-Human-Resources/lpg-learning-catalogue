package uk.gov.cslearning.catalogue.service.upload;

import uk.gov.cslearning.catalogue.dto.FileUpload;

public interface FileManagementService {
    FileUpload create(FileUpload fileUpload);
}
