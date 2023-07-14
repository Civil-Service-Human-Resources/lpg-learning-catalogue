package uk.gov.cslearning.catalogue.domain;

public enum CustomMediaMetadata {

    ELEARNING_MANIFEST("elearning_manifest");

    private final String metadataKey;

    CustomMediaMetadata(String metadataKey) {
        this.metadataKey = metadataKey;
    }

    public String getMetadataKey() {
        return metadataKey;
    }
}
