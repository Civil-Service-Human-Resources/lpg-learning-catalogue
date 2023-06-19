package uk.gov.cslearning.catalogue.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import uk.gov.cslearning.catalogue.service.upload.UploadServiceType;

@Data
@AllArgsConstructor
public class FileMapping {

    private final String ext;
    private final UploadServiceType serviceType;
    private final String moduleType;
}
