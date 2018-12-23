package uk.gov.cslearning.catalogue.api;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.cslearning.catalogue.domain.Media;
import uk.gov.cslearning.catalogue.service.upload.FileUploadFactory;
import uk.gov.cslearning.catalogue.service.upload.MediaManagementService;

import javax.servlet.http.HttpServletRequest;
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

        return (ResponseEntity.created(builder.path("/media/{mediaUid}").build(media.getId()))
                .header("Access-Control-Expose-Headers",  "Location")).build();
    }

    //Work around for uploading without js enabled
    @PostMapping("/nojs")
    public ResponseEntity<Void> uploadWithNoJs(MultipartFile file, @RequestParam String container, @RequestParam(required = false) String filename, HttpServletRequest request) {
        Media media = mediaManagementService.create(fileUploadFactory.create(file, container, filename));
        String referrer = request.getHeader("referer");
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(referrer + "/{id}").buildAndExpand(media.getId());
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriComponents.toUri());
        return new ResponseEntity<Void>(headers, HttpStatus.FOUND);
    }

    @GetMapping("/{mediaId}")
    public ResponseEntity<Media> read(@PathVariable String mediaId) {
        Optional<Media> media = mediaManagementService.findById(mediaId);

        return media.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
