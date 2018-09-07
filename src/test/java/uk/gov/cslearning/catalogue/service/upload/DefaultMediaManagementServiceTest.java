package uk.gov.cslearning.catalogue.service.upload;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.catalogue.domain.Media;
import uk.gov.cslearning.catalogue.domain.MediaFactory;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.Upload;
import uk.gov.cslearning.catalogue.repository.MediaRepository;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultMediaManagementServiceTest {
    @Mock
    private MediaFactory mediaFactory;

    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private FileUploadService fileUploadService;

    private DefaultMediaManagementService mediaManagementService;

    @Before
    public void setUp() {
        mediaManagementService = new DefaultMediaManagementService(mediaFactory, mediaRepository, fileUploadService);
    }

    @Test
    public void shouldUploadFileAndReturnMedia() {
        FileUpload fileUpload = mock(FileUpload.class);
        Upload upload = mock(Upload.class);
        Media media = mock(Media.class);
        Media savedMedia = mock(Media.class);

        when(fileUploadService.upload(fileUpload)).thenReturn(upload);
        when(mediaFactory.create(upload)).thenReturn(media);
        when(mediaRepository.save(media)).thenReturn(savedMedia);

        assertEquals(savedMedia, mediaManagementService.create(fileUpload));
    }

    @Test
    public void findByIdReturnsMediaOptional() {
        String mediaId = "media-id";
        Optional<Media> optional = Optional.empty();

        when(mediaRepository.findById(mediaId)).thenReturn(optional);

        assertEquals(optional, mediaManagementService.findById(mediaId));
    }
}