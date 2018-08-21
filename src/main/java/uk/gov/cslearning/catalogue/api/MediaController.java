package uk.gov.cslearning.catalogue.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.catalogue.domain.media.Media;
import uk.gov.cslearning.catalogue.service.FileUploadFactory;
import uk.gov.cslearning.catalogue.service.MediaManagementService;

@Controller
@RequestMapping("/media")
public class MediaController {

    private final MediaManagementService mediaManagementService;
    private final FileUploadFactory fileUploadFactory;

    public MediaController(MediaManagementService mediaManagementService, FileUploadFactory fileUploadFactory) {
        this.mediaManagementService = mediaManagementService;
        this.fileUploadFactory = fileUploadFactory;
    }

    @PostMapping
    public ResponseEntity<Void> upload(MultipartFile file, @RequestParam String container, UriComponentsBuilder builder) {

        Media media = mediaManagementService.create(fileUploadFactory.create(file, container));

        return ResponseEntity.created(builder.path("/service/media/{mediaUid}").build(media.getUid())).build();
    }
}
