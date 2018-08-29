package uk.gov.cslearning.catalogue.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.catalogue.domain.media.Document;
import uk.gov.cslearning.catalogue.domain.media.MediaEntity;
import uk.gov.cslearning.catalogue.domain.media.MediaEntityFactory;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.Upload;
import uk.gov.cslearning.catalogue.repository.MediaRepository;
import uk.gov.cslearning.catalogue.service.upload.DefaultMediaManagementService;
import uk.gov.cslearning.catalogue.service.upload.FileUploadService;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultMediaManagementServiceTest {
    @Mock
    private MediaEntityFactory mediaEntityFactory;

    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private FileUploadService fileUploadService;

    private DefaultMediaManagementService mediaManagementService;

    @Before
    public void setUp() {
        mediaManagementService = new DefaultMediaManagementService(mediaEntityFactory, mediaRepository, fileUploadService);
    }

    @Test
    public void shouldUploadFileAndReturnMedia() {
        FileUpload fileUpload = mock(FileUpload.class);
        Upload upload = mock(Upload.class);
        Document document = mock(Document.class);
        Document savedDocument = mock(Document.class);

        when(fileUploadService.upload(fileUpload)).thenReturn(upload);
        when(mediaEntityFactory.create(upload)).thenReturn(document);
        when(mediaRepository.save(document)).thenReturn(savedDocument);

        assertEquals(savedDocument, mediaManagementService.create(fileUpload));
    }

    @Test
    public void findByIdReturnsMediaOptional() {
        String mediaId = "media-id";
        Optional<MediaEntity> optional = Optional.empty();

        when(mediaRepository.findById(mediaId)).thenReturn(optional);

        assertEquals(optional, mediaManagementService.findByUid(mediaId));
    }
}