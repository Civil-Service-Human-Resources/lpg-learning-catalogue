package uk.gov.cslearning.catalogue.dto.factory;

import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.domain.media.Document;
import uk.gov.cslearning.catalogue.domain.media.MediaEntity;
import uk.gov.cslearning.catalogue.dto.FileUpload;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;

@Component
public class MediaFactory {

    private Map<String, Function<FileUpload, MediaEntity>> createMethods = ImmutableMap.of(
            "doc", fileUpload -> {
                Document document = new Document();
                document.setContainer(fileUpload.getContainer());
                document.setDateAdded(LocalDateTime.now());
                document.setExtension(fileUpload.getExtension());
                document.setName(fileUpload.getName());
                document.setPath("/".concat(String.join("/", fileUpload.getContainer(), fileUpload.getName())));
                document.setUid(fileUpload.getContainer());
                document.setFileSize(fileUpload.getSize());

                return document;
            }
    );



    public MediaEntity create(FileUpload fileUpload) {
        return createMethods.get(fileUpload.getExtension()).apply(fileUpload);
    }
}
