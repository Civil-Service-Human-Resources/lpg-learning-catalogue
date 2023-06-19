package uk.gov.cslearning.catalogue.service.upload.processor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.cslearning.catalogue.dto.upload.FileUpload;
import uk.gov.cslearning.catalogue.exception.FileValidationException;
import uk.gov.cslearning.catalogue.service.upload.processor.validator.ImageFileValidator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ImageIO.class})
public class ImageFileValidatorTest {

    private final ImageFileValidator imageFileValidator = new ImageFileValidator();

    @Test(expected = FileValidationException.class)
    public void testValidateImageSize() {
        FileUpload fileUpload = mock(FileUpload.class);

        when(fileUpload.getSizeKB()).thenReturn(5121L);
        imageFileValidator.validate(fileUpload);
    }

    @Test(expected = FileValidationException.class)
    public void testValidateImageDimensions() throws IOException {
        PowerMockito.mockStatic(ImageIO.class);
        FileUpload fileUpload = mock(FileUpload.class);
        MultipartFile file = mock(MultipartFile.class);
        InputStream inputStream = mock(InputStream.class);
        BufferedImage bufferedImage = new BufferedImage(970, 650, BufferedImage.TYPE_INT_RGB);
        when(ImageIO.read(inputStream)).thenReturn(bufferedImage);

        when(fileUpload.getSizeKB()).thenReturn(5100L);
        when(file.getInputStream()).thenReturn(inputStream);
        when(fileUpload.getFile()).thenReturn(file);
        imageFileValidator.validate(fileUpload);
    }
}
