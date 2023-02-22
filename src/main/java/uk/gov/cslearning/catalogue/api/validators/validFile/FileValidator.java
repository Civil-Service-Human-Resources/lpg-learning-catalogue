package uk.gov.cslearning.catalogue.api.validators.validFile;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.cslearning.catalogue.config.FileUploadMap;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Locale;

@Component
public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {

    private final FileUploadMap fileUploadMap;

    public FileValidator(FileUploadMap fileUploadMap) {
        this.fileUploadMap = fileUploadMap;
    }

    @Override
    public void initialize(ValidFile constraintAnnotation) { }

    @Override
    public boolean isValid(MultipartFile multipartFile, ConstraintValidatorContext context) {
        context.disableDefaultConstraintViolation();

        if (multipartFile == null) {
            customMessageForValidation(context, "File is required");
            return false;
        }
        String fileExt = FilenameUtils.getExtension(multipartFile.getOriginalFilename());

        if (fileExt != null) {
            List<String> validFileExts = fileUploadMap.getValidFileExts();
            if (!validFileExts.contains(fileExt.toLowerCase(Locale.ROOT))) {
                customMessageForValidation(context, String.format("File extension must be one of %s", String.join(",", validFileExts)));
                return false;
            }
        }

        return true;
    }

    private void customMessageForValidation(ConstraintValidatorContext constraintContext, String message) {
        // build new violation message and add it
        constraintContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }

}
