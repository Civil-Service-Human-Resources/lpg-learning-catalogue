package uk.gov.cslearning.catalogue.service;

import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.Media;

public interface MediaManagementService {
    Media create(FileUpload fileUpload);
}
