package uk.gov.cslearning.catalogue.api;

import lombok.Data;

@Data
public class OwnerParameters {
    String profession;
    String organisationalUnitCode;
    String supplier;

    public boolean hasProfession() {
        return this.profession != null && !this.profession.isEmpty();
    }

    public boolean hasOrganisationalUnitCode() {
        return this.organisationalUnitCode != null && !this.organisationalUnitCode.isEmpty();
    }

    public boolean hasSupplier() {
        return this.supplier != null && !this.supplier.isEmpty();
    }
}
