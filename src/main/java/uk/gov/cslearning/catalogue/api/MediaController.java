package uk.gov.cslearning.catalogue.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.cslearning.catalogue.service.FileUploadFactory;
import uk.gov.cslearning.catalogue.service.MediaManagementService;

@Controller
@RequestMapping("/service/media")
public class MediaController {

    private final MediaManagementService mediaManagementService;
    private final FileUploadFactory fileUploadFactory;

    public MediaController(MediaManagementService mediaManagementService, FileUploadFactory fileUploadFactory) {
        this.mediaManagementService = mediaManagementService;
        this.fileUploadFactory = fileUploadFactory;
    }

    @PostMapping
    public ResponseEntity<Void> upload(MultipartFile file, @RequestParam String container) {

        mediaManagementService.create(fileUploadFactory.create(file, container));

        return ResponseEntity.noContent().build();
    }
}
