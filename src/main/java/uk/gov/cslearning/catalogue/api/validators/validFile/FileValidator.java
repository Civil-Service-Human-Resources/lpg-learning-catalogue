package uk.gov.cslearning.catalogue.api.validators.validFile;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.cslearning.catalogue.service.upload.FileUploadServiceFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Locale;

@Component
public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

    private final FileUploadServiceFactory fileUploadServiceFactory;

    public FileValidator(FileUploadServiceFactory fileUploadServiceFactory) {
        this.fileUploadServiceFactory = fileUploadServiceFactory;
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
        return fileUploadServiceFactory.getValidFileExts().contains(fileExt);
    }
}
