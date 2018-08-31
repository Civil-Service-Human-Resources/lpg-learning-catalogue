package uk.gov.cslearning.catalogue.service.upload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.domain.media.Media;
import uk.gov.cslearning.catalogue.domain.media.MediaEntity;
import uk.gov.cslearning.catalogue.domain.media.MediaEntityFactory;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.Upload;
import uk.gov.cslearning.catalogue.repository.MediaRepository;

import java.util.Optional;

@Service
public class DefaultMediaManagementService implements MediaManagementService {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultMediaManagementService.class);

    private final MediaEntityFactory mediaEntityFactory;
    private final MediaRepository mediaRepository;
    private final FileUploadService fileUploadService;

    public DefaultMediaManagementService(MediaEntityFactory mediaEntityFactory, MediaRepository mediaRepository, FileUploadService fileUploadService) {
        this.mediaEntityFactory = mediaEntityFactory;
        this.mediaRepository = mediaRepository;
        this.fileUploadService = fileUploadService;
    }

    @Override
    public MediaEntity create(FileUpload fileUpload) {
        Upload upload = fileUploadService.upload(fileUpload);
        Media media = mediaEntityFactory.create(upload);

        return mediaRepository.save((MediaEntity) media);
    }

    @Override
    public Optional<MediaEntity> findById(String mediaId) {
        return mediaRepository.findById(mediaId);
    }
}
