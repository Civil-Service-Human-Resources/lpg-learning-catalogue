package uk.gov.cslearning.catalogue.service.upload;

import uk.gov.cslearning.catalogue.domain.Media;
import uk.gov.cslearning.catalogue.dto.FileUpload;

import java.io.IOException;
import java.util.Optional;

public interface MediaManagementService {
    Media create(FileUpload fileUpload);

    Media createImage(FileUpload fileUpload) throws IOException;

    Optional<Media> findById(String mediaId);
}
