package uk.gov.cslearning.catalogue.service.upload.processor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.exception.FileUploadException;
import uk.gov.cslearning.catalogue.exception.UnknownFileTypeException;

@Component
public class ImageProcessorFactory {
    private final Map<String, FileProcessor> fileProcessorMap;

    public ImageProcessorFactory(@Qualifier("imageProcessorMap") Map<String, FileProcessor> fileProcessorMap) {
        this.fileProcessorMap = fileProcessorMap;
    }

    public FileProcessor create(FileUpload fileUpload) {

        if (fileProcessorMap.containsKey(fileUpload.getExtension().toLowerCase())) {
            return fileProcessorMap.get(fileUpload.getExtension().toLowerCase());
        } else {
            throw new UnknownFileTypeException(
                    String.format("Uploaded file has an unknown extension: %s",
                            fileUpload.getExtension()));
        }
    }

    public void validateProcessedFile(FileUpload fileUpload) throws IOException {
        if (fileUpload.getSizeKB() > 5120) {
            throw new FileUploadException(
                    "Uploaded file exceeds maximum allowed size of 5MB");
        }

        final File tempFile = File.createTempFile("temp_image_" + LocalDateTime.now(),fileUpload.getExtension(),null);
        FileOutputStream fos = new FileOutputStream(tempFile);
        final byte[] fileAsBytes = fileUpload.getFile().getBytes();
        int height, width, dpi = 0;

        try {

            fos.write(fileAsBytes);
            fos.close();
            BufferedImage bImg = ImageIO.read(tempFile);
            height = bImg.getHeight();
            width = bImg.getWidth();

        } catch (Exception ex) {
            throw new IOException(
                    String.format("Reading file encountered an error: %s",
                            ex.getMessage()));
        }

        if (width > 960 && height > 640) {
            throw new FileUploadException(
                    "Uploaded file does not meet the required dimensions." +
                            " 960p width X 640p height");
        }
    }
}
