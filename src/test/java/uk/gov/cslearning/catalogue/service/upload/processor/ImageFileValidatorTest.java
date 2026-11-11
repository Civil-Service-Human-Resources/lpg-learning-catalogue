package uk.gov.cslearning.catalogue.service.upload.processor;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.cslearning.catalogue.dto.upload.FileUpload;
import uk.gov.cslearning.catalogue.exception.FileValidationException;
import uk.gov.cslearning.catalogue.service.upload.processor.validator.ImageFileValidator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ImageFileValidatorTest {

    private final ImageFileValidator imageFileValidator = new ImageFileValidator();

    @Test
    public void testValidateImageSize() {
        FileUpload fileUpload = mock(FileUpload.class);

        when(fileUpload.getSizeKB()).thenReturn(5121L);
        assertThrows(FileValidationException.class, () -> imageFileValidator.validate(fileUpload));
    }

    @Test
    public void testValidateImageDimensions() throws IOException {
        try (MockedStatic<ImageIO> mockedImageIO = Mockito.mockStatic(ImageIO.class)) {
            FileUpload fileUpload = mock(FileUpload.class);
            MultipartFile file = mock(MultipartFile.class);
            InputStream inputStream = mock(InputStream.class);
            BufferedImage bufferedImage = new BufferedImage(970, 650, BufferedImage.TYPE_INT_RGB);

            // Correctly stub the static ImageIO.read method
            mockedImageIO.when(() -> ImageIO.read(any(InputStream.class))).thenReturn(bufferedImage);

            when(fileUpload.getSizeKB()).thenReturn(5100L);
            when(file.getInputStream()).thenReturn(inputStream);
            when(fileUpload.getFile()).thenReturn(file);
            assertThrows(FileValidationException.class, () -> imageFileValidator.validate(fileUpload));
        }
    }
}
