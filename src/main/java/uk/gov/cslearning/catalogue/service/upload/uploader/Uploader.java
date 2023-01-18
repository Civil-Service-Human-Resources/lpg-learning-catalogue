package uk.gov.cslearning.catalogue.service.upload.uploader;

import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.dto.Upload;

public interface Uploader {
    Upload upload(ProcessedFile processedFile);
}
