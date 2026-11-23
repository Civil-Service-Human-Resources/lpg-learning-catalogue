package uk.gov.cslearning.catalogue.domain;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MediaTest {
    @Test
    public void formatFileSizeShouldReturnHumanReadableString_2GB() {
        Media media = new Media();
        media.setFileSizeKB(2074636);

        assertEquals("2 GB", media.formatFileSize());
    }

    @Test
    public void formatFileSizeShouldReturnHumanReadableString_0() {
        Media media = new Media();
        media.setFileSizeKB(0);

        assertEquals("0", media.formatFileSize());
    }


    @Test
    public void formatFileSizeShouldReturnHumanReadableString_5GB() {
        Media media = new Media();
        media.setFileSizeKB((1024 * 1024 * 5) - 10000);

        assertEquals("5 GB", media.formatFileSize());
    }

}