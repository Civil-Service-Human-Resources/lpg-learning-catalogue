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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ScormManagerTest {
    @Mock
    private FileFactory fileFactory;

    @Mock
    private InputStreamFactory inputStreamFactory;

    @Mock
    private UploadClient uploadClient;

    @Mock
    private Map<String, String> fileSubstitutions;

    @InjectMocks
    private ScormManager scormManager;

    @Test
    public void isReplacementCandidateReturnsFalseIfNotInMap() {
        String path = "test-path";
        when(fileSubstitutions.containsKey(path)).thenReturn(false);
        assertFalse(scormManager.isReplacementCandidate(path));
    }

    @Test
    public void isReplacementCandidateReturnsTrueIfInMap() {
        String path = "test-path";
        when(fileSubstitutions.containsKey(path)).thenReturn(true);
        assertTrue(scormManager.isReplacementCandidate(path));
    }

    @Test
    public void ignoreOrReplaceReturnsEmptyOptionalIfEntryValueIsEmpty() throws IOException, URISyntaxException {
        String zipPath = "test-path";
        when(fileSubstitutions.get(zipPath)).thenReturn("");
        assertEquals(Optional.empty(), scormManager.ignoreOrReplace(zipPath, null));
    }


    @Test
    public void ignoreOrReplaceReturnsOptionalUploadedFileifEntryValueIsPresent() throws URISyntaxException, IOException {
        long fileLength = 999;
        String zipPath = "test-path";
        String replacementPath = "replacement-path";
        String destinationPath = "destination-path";

        when(fileSubstitutions.get(zipPath)).thenReturn(replacementPath);

        File file = mock(File.class);
        when(file.length()).thenReturn(fileLength);
        when(fileFactory.get(replacementPath)).thenReturn(file);

        InputStream inputStream = mock(InputStream.class);
        when(inputStreamFactory.createFileInputStream(file)).thenReturn(inputStream);

        UploadedFile uploadedFile = mock(UploadedFile.class);
        when(uploadClient.upload(inputStream, destinationPath, fileLength)).thenReturn(uploadedFile);

        assertEquals(Optional.of(uploadedFile), scormManager.ignoreOrReplace(zipPath, destinationPath));
    }
}