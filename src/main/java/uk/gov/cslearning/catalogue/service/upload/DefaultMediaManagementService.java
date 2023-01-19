package uk.gov.cslearning.catalogue.service.upload;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.domain.Media;
import uk.gov.cslearning.catalogue.domain.MediaFactory;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.upload.Upload;
import uk.gov.cslearning.catalogue.repository.MediaRepository;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class DefaultMediaManagementService implements MediaManagementService {
    private final MediaFactory mediaFactory;
    private final MediaRepository mediaRepository;
    private final Map<String, FileUploadService> fileUploadServiceMap;

    public DefaultMediaManagementService(MediaFactory mediaFactory, MediaRepository mediaRepository,
                                         @Qualifier("fileUploadServiceMap") Map<String,
                                         FileUploadService> fileUploadServiceMap) {
        this.mediaFactory = mediaFactory;
        this.mediaRepository = mediaRepository;
        this.fileUploadServiceMap = fileUploadServiceMap;
    }

    @Override
    public Media create(FileUpload fileUpload) {
        String fileExt = fileUpload.getExtension().toLowerCase(Locale.ROOT);
        FileUploadService fileUploadService = fileUploadServiceMap.get(fileExt);
        Upload upload = fileUploadService.upload(fileUpload);
        Media media = mediaFactory.create(upload);

        return mediaRepository.save(media);
    }

    @Override
    public Optional<Media> findById(String mediaId) {
        return mediaRepository.findById(mediaId);
    }
}
