package uk.gov.cslearning.catalogue.api;

import lombok.Data;

@Data
public class OwnerParameters {
    String profession;
    String organisationalUnitCode;
    String learningProviderId;

    public boolean hasProfession() {
        return this.profession != null && !this.profession.isEmpty();
    }

    public boolean hasOrganisationalUnitCode() {
        return this.organisationalUnitCode != null && !this.organisationalUnitCode.isEmpty();
    }

    public boolean hasLearningProviderId() {
        return this.learningProviderId != null && !this.learningProviderId.isEmpty();
    }
}
