package uk.gov.cslearning.catalogue.service.upload.uploader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.catalogue.dto.UploadedFile;
import uk.gov.cslearning.catalogue.service.upload.FileFactory;
import uk.gov.cslearning.catalogue.service.upload.InputStreamFactory;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;
import uk.gov.cslearning.catalogue.service.upload.processor.MetadataParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SubstituteZipEntryUploaderTest {

    @Mock
    private FileFactory fileFactory;

    @Mock
    private InputStreamFactory inputStreamFactory;

    @Mock
    private Map<String, String> fileSubstitutions;

    @Mock
    private MetadataParser metadataParser;

    @InjectMocks
    private SubstituteZipEntryUploader uploader;

    @Test
    public void shouldUploadReplacementFileAndReturnOptionalUploadedFile() throws URISyntaxException, IOException {
        String destinationPath = "destination-path";
        String zipEntryName = "zip-entry";
        String substitutePath = "substitute-path";
        String contentType = "content-type";

        ZipEntry zipEntry = mock(ZipEntry.class);
        when(zipEntry.getName()).thenReturn(zipEntryName);

        UploadClient uploadClient = mock(UploadClient.class);
        when(fileSubstitutions.get(zipEntryName)).thenReturn(substitutePath);

        File file = mock(File.class);
        long fileLength = 999;
        when(file.length()).thenReturn(fileLength);
        when(fileFactory.get(substitutePath)).thenReturn(file);

        InputStream inputStream = mock(InputStream.class);
        when(inputStreamFactory.createFileInputStream(file)).thenReturn(inputStream);

        when(metadataParser.getContentType(inputStream, zipEntryName)).thenReturn(contentType);

        UploadedFile uploadedFile = mock(UploadedFile.class);
        when(uploadClient.upload(inputStream, destinationPath, fileLength, contentType)).thenReturn(uploadedFile);

        Optional<UploadedFile> result = uploader.upload(uploadClient, zipEntry, inputStream, destinationPath);

        assertTrue(result.isPresent());
        assertEquals(uploadedFile, result.get());
    }
}