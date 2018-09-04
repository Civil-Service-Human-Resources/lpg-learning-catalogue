package uk.gov.cslearning.catalogue.service.upload.uploader;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.zip.ZipEntry;

@Component
public class ZipEntryUploaderFactory {
    private final Map<String, String> fileSubstitutions;
    private final DummyZipEntryUploader dummyZipEntryUploader;
    private final SubstituteZipEntryUploader substituteZipEntryUploader;
    private final DefaultZipEntryUploader defaultZipEntryUploader;

    public ZipEntryUploaderFactory(Map<String, String> fileSubstitutions, DummyZipEntryUploader dummyZipEntryUploader,
                                   SubstituteZipEntryUploader substituteZipEntryUploader,
                                   DefaultZipEntryUploader defaultZipEntryUploader) {
        this.fileSubstitutions = fileSubstitutions;
        this.dummyZipEntryUploader = dummyZipEntryUploader;
        this.substituteZipEntryUploader = substituteZipEntryUploader;
        this.defaultZipEntryUploader = defaultZipEntryUploader;
    }

    public ZipEntryUploader get(ZipEntry zipEntry) {
        if (fileSubstitutions.containsKey(zipEntry.getName())) {
            if (fileSubstitutions.get(zipEntry.getName()).isEmpty()) {
                return dummyZipEntryUploader;
            }
            return substituteZipEntryUploader;
        }

        return defaultZipEntryUploader;
    }
}
