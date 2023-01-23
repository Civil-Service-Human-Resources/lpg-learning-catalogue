package uk.gov.cslearning.catalogue.api.validators.validFile;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.cslearning.catalogue.service.upload.FileUploadService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Locale;
import java.util.Map;

@Component
public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

    @Qualifier("fileUploadServiceMap")
    private final Map<String, FileUploadService> fileUploadServiceMap;

    public FileValidator(Map<String, FileUploadService> fileUploadServiceMap) {
        this.fileUploadServiceMap = fileUploadServiceMap;
    }

    @Override
    public void initialize(ValidFile constraintAnnotation) { }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {

        boolean result = false;
        String filename = FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        if (filename != null) {
            String fileExt = filename.toLowerCase(Locale.ROOT);
            if (isSupportedFileExt(fileExt)) {
                result = true;
            }
        }

        return result;
    }

    private boolean isSupportedFileExt(String fileExt) {
        return fileUploadServiceMap.containsKey(fileExt);
    }
}
