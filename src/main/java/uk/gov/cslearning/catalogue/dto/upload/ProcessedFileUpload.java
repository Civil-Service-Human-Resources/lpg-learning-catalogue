package uk.gov.cslearning.catalogue.dto.upload;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Data
@RequiredArgsConstructor
public class ProcessedFileUpload {
    private final FileUpload fileUpload;
    private final List<UploadableFile> uploadableFiles;
    private Map<String, String> metadata = Collections.emptyMap();

    public ProcessedFileUpload(FileUpload fileUpload,
                                List<UploadableFile> uploadableFiles, Map<String, String> metadata) {
        this(fileUpload, uploadableFiles);
        this.metadata = metadata;
    }
}
