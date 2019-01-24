package uk.gov.cslearning.catalogue.api;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class OwnerParametersTest {

    @Test
    public void shouldReturnFalseIfNoProfession() {
        OwnerParameters ownerParameters = new OwnerParameters();
        assertFalse(ownerParameters.hasProfession());
    }

    @Test
    public void hasOrganisationalUnitCode() {
    }
}