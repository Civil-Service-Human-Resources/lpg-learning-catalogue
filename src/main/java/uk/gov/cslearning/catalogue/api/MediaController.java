package uk.gov.cslearning.catalogue.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.catalogue.domain.Media;
import uk.gov.cslearning.catalogue.service.upload.FileUploadFactory;
import uk.gov.cslearning.catalogue.service.upload.MediaManagementService;

import java.util.Optional;


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
    public ResponseEntity<Void> upload(MultipartFile file, @RequestParam String container, @RequestParam(required = false) String filename, UriComponentsBuilder builder) {

        Media media = mediaManagementService.create(fileUploadFactory.create(file, container, filename));

        return ResponseEntity.created(builder.path("/media/{mediaUid}").build(media.getId())).build();
    }

    @GetMapping("/{mediaId}")
    public ResponseEntity<Media> read(@PathVariable String mediaId) {
        Optional<Media> media = mediaManagementService.findById(mediaId);

        return media.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
