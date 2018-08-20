package uk.gov.cslearning.catalogue.service;

import uk.gov.cslearning.catalogue.domain.media.Media;
import uk.gov.cslearning.catalogue.dto.FileUpload;

public interface MediaManagementService {
    Media create(FileUpload fileUpload);
}
