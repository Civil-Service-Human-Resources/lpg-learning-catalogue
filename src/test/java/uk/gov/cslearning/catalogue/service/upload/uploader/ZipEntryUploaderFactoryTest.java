package uk.gov.cslearning.catalogue.service.upload.uploader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Map;
import java.util.zip.ZipEntry;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ZipEntryUploaderFactoryTest {

    @Mock
    private Map<String, String> fileSubstitutions;

    @Mock
    private DummyZipEntryUploader dummyZipEntryUploader;

    @Mock
    private SubstituteZipEntryUploader substituteZipEntryUploader;

    @Mock
    private DefaultZipEntryUploader defaultZipEntryUploader;

    @InjectMocks
    private ZipEntryUploaderFactory factory;

    @Test
    public void shouldReturnDummyUploaderForSubstitutionWithEmptyValue() {
        String zipEntryName = "zip-entry-name";

        ZipEntry zipEntry = mock(ZipEntry.class);
        when(zipEntry.getName()).thenReturn(zipEntryName);

        when(fileSubstitutions.containsKey(zipEntryName)).thenReturn(true);
        when(fileSubstitutions.get(zipEntryName)).thenReturn("");

        ZipEntryUploader uploader = factory.get(zipEntry);

        assertTrue(uploader instanceof DummyZipEntryUploader);
    }

    @Test
    public void shouldReturnSubstituteUploaderForSubstitutionWithValuePresent() {
        String zipEntryName = "zip-entry-name";

        ZipEntry zipEntry = mock(ZipEntry.class);
        when(zipEntry.getName()).thenReturn(zipEntryName);

        when(fileSubstitutions.containsKey(zipEntryName)).thenReturn(true);
        when(fileSubstitutions.get(zipEntryName)).thenReturn("substitution-path");

        ZipEntryUploader uploader = factory.get(zipEntry);

        assertTrue(uploader instanceof SubstituteZipEntryUploader);
    }

    @Test
    public void shouldReturnDefaultUploaderWhenNoSubstitutionPresent() {
        String zipEntryName = "zip-entry-name";

        ZipEntry zipEntry = mock(ZipEntry.class);
        when(zipEntry.getName()).thenReturn(zipEntryName);

        when(fileSubstitutions.containsKey(zipEntryName)).thenReturn(false);

        ZipEntryUploader uploader = factory.get(zipEntry);

        assertTrue(uploader instanceof DefaultZipEntryUploader);
    }

}