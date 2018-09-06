package uk.gov.cslearning.catalogue.service.upload.processor;

public enum ProcessedFileMetadataKeys {
    DURATION("duration"),
    IMAGE_WIDTH("imageWidth"),
    IMAGE_HEIGHT("imageHeight");

    private final String value;

    ProcessedFileMetadataKeys(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
