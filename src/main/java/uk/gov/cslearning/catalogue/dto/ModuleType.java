package uk.gov.cslearning.catalogue.dto;

public enum ModuleType {
    FACE_TO_FACE("face-to-face"),ELEARNING("elearning");

    private final String value;

    ModuleType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
