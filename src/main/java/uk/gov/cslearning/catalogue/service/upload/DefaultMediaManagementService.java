package uk.gov.cslearning.catalogue.service.upload;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.domain.Media;
import uk.gov.cslearning.catalogue.domain.MediaFactory;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.Upload;
import uk.gov.cslearning.catalogue.repository.MediaRepository;

import java.util.Optional;

@Service
public class DefaultMediaManagementService implements MediaManagementService {
    private final MediaFactory mediaFactory;
    private final MediaRepository mediaRepository;
    private final FileUploadService fileUploadService;

    public DefaultMediaManagementService(MediaFactory mediaFactory, MediaRepository mediaRepository, FileUploadService fileUploadService) {
        this.mediaFactory = mediaFactory;
        this.mediaRepository = mediaRepository;
        this.fileUploadService = fileUploadService;
    }

    @Override
    public Media create(FileUpload fileUpload) {
        Upload upload = fileUploadService.upload(fileUpload);
        Media media = mediaFactory.create(upload);

        return mediaRepository.save((Media) media);
    }

    @Override
    public Optional<Media> findById(String mediaId) {
        return mediaRepository.findById(mediaId);
    }
}
