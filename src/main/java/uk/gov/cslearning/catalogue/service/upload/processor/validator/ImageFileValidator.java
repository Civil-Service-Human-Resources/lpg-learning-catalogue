package uk.gov.cslearning.catalogue.service.upload.processor.validator;

import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.upload.FileUpload;
import uk.gov.cslearning.catalogue.exception.FileValidationException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@Component
public class ImageFileValidator implements FileValidator {

    private void checkSize(long sizeKB) {
        if (sizeKB > 5120) {
            throw new FileValidationException(
                    "Uploaded file exceeds maximum allowed size of 5MB");
        }
    }

    private void checkDimensions(FileUpload fileUpload) {
        try (InputStream in = fileUpload.getFile().getInputStream()) {
            BufferedImage bImg = ImageIO.read(in);
            if (bImg.getWidth() > 960 && bImg.getHeight() > 640) {
                throw new FileValidationException(
                        "Uploaded file does not meet the required dimensions." +
                                " 960p width X 640p height");
            }
        } catch (IOException ex) {
            throw new FileValidationException(String.format("Reading file encountered an error: %s", ex));
        }
    }

    @Override
    public void validate(FileUpload fileUpload) throws FileValidationException {
        checkSize(fileUpload.getSizeKB());
        checkDimensions(fileUpload);
    }
}
