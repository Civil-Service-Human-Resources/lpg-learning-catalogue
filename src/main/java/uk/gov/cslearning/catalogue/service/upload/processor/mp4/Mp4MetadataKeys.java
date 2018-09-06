package uk.gov.cslearning.catalogue.service.upload.processor.mp4;

public enum Mp4MetadataKeys {
    DURATION("xmpDM:duration"),
    IMAGE_WIDTH("tiff:ImageWidth"),
    IMAGE_HEIGHT("tiff:ImageLength");

    private final String value;

    Mp4MetadataKeys(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
