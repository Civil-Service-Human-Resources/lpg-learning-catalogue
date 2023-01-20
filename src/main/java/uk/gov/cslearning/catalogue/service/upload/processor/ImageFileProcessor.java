package uk.gov.cslearning.catalogue.service.upload.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.dto.upload.FileUpload;
import uk.gov.cslearning.catalogue.dto.upload.UploadableFile;
import uk.gov.cslearning.catalogue.dto.upload.ProcessedFileUpload;
import uk.gov.cslearning.catalogue.exception.FileProcessingException;
import uk.gov.cslearning.catalogue.exception.FileUploadException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;

@Service
@Slf4j
public class ImageFileProcessor implements FileProcessor {
    @Override
    public ProcessedFileUpload process(FileUpload fileUpload) throws FileProcessingException {
        try {
            validateProcessedFile(fileUpload);
            UploadableFile uploadableFile = UploadableFile.createFromFileUpload(fileUpload);
            return new ProcessedFileUpload(fileUpload, Collections.singletonList(uploadableFile));
        } catch (Exception e) {
            log.error(String.format("Failed to process image file \"%s\"", fileUpload.getName()));
            throw new FileUploadException(e);
        }
    }

    private void validateProcessedFile(FileUpload fileUpload) throws IOException, FileUploadException {
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
