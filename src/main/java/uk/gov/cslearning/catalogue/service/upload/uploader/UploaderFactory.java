package uk.gov.cslearning.catalogue.service.upload.uploader;

import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.exception.UnknownFileTypeException;

import java.util.Map;
import java.util.function.Supplier;

@Component
public class UploaderFactory {

    private final DefaultUploader defaultUploader;
    private final ScormUploader scormUploader;

    private final Map<String, Supplier<Uploader>> createSuppliers = ImmutableMap.of(
        "doc", new Supplier<Uploader>() {
            @Override
            public Uploader get() {
                return defaultUploader;
            }},
        "scorm",new Supplier<Uploader>() {
                @Override
                public Uploader get() {
                    return scormUploader;
                }
        });

    public UploaderFactory(DefaultUploader defaultUploader, ScormUploader scormUploader) {
        this.defaultUploader = defaultUploader;
        this.scormUploader = scormUploader;
    }

    public Uploader create(ProcessedFile processedFile) {
        String extension = processedFile.getFileUpload().getExtension();

        if (createSuppliers.containsKey(extension)) {
            return createSuppliers.get(extension).get();
        }

        throw new UnknownFileTypeException(String.format("Uploaded file has an unknown extension: %s", extension));
    }
}
