package uk.gov.cslearning.catalogue.api;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OwnerParametersTest {

    @Test
    public void shouldReturnFalseIfNoProfession() {
        OwnerParameters ownerParameters = new OwnerParameters();
        assertFalse(ownerParameters.hasProfession());
    }

    @Test
    public void shouldReturnFalseIfNoProfessionIsEmpty() {
        OwnerParameters ownerParameters = new OwnerParameters();
        ownerParameters.setProfession("");

        assertFalse(ownerParameters.hasProfession());
    }

    @Test
    public void shouldReturnTrueIfProfession() {
        OwnerParameters ownerParameters = new OwnerParameters();
        ownerParameters.setProfession("prof");

        assertTrue(ownerParameters.hasProfession());
    }

    @Test
    public void shouldReturnFalseIfNoOrganisation() {
        OwnerParameters ownerParameters = new OwnerParameters();
        assertFalse(ownerParameters.hasOrganisationalUnitCode());
    }

    @Test
    public void shouldReturnFalseIfNoOrganisationIsEmpty() {
        OwnerParameters ownerParameters = new OwnerParameters();
        ownerParameters.setOrganisationalUnitCode("");

        assertFalse(ownerParameters.hasOrganisationalUnitCode());
    }

    @Test
    public void shouldReturnTrueIfOrganisation() {
        OwnerParameters ownerParameters = new OwnerParameters();
        ownerParameters.setOrganisationalUnitCode("code");

        assertTrue(ownerParameters.hasOrganisationalUnitCode());
    }

    @Test
    public void shouldReturnFalseIfNoLearningProvider() {
        OwnerParameters ownerParameters = new OwnerParameters();
        assertFalse(ownerParameters.hasSupplier());
    }

    @Test
    public void shouldReturnFalseIfLearningProviderIsEmpty() {
        OwnerParameters ownerParameters = new OwnerParameters();
        ownerParameters.setSupplier("");

        assertFalse(ownerParameters.hasSupplier());
    }

    @Test
    public void shouldReturnTrueIfLearningProvider() {
        OwnerParameters ownerParameters = new OwnerParameters();
        ownerParameters.setSupplier("lp");

        assertTrue(ownerParameters.hasSupplier());
    }
}