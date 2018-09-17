package uk.gov.cslearning.catalogue.service.upload.processor;

import com.google.common.collect.ImmutableMap;
import org.apache.tika.exception.TikaException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.dto.ProcessedFileFactory;
import uk.gov.cslearning.catalogue.exception.FileUploadException;
import uk.gov.cslearning.catalogue.service.upload.processor.mp4.Mp4FileProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class Mp4FileProcessorTest {
    @Mock
    private ProcessedFileFactory processedFileFactory;

    @Mock
    private MetadataParser metadataParser;

    @InjectMocks
    private Mp4FileProcessor mp4FileProcessor;

    @Test
    public void processShouldSetMetadata() throws IOException, TikaException, SAXException {
        Map<String, String> metadataMap = ImmutableMap.of(
                "key1", "value1",
                "key2", ""
        );

        MultipartFile multipartFile = mock(MultipartFile.class);
        InputStream inputStream = mock(InputStream.class);
        when(multipartFile.getInputStream()).thenReturn(inputStream);
        FileUpload fileUpload = mock(FileUpload.class);
        when(fileUpload.getFile()).thenReturn(multipartFile);

        when(metadataParser.parse(inputStream)).thenReturn(metadataMap);

        ProcessedFile processedFile = mock(ProcessedFile.class);
        when(processedFileFactory.create(fileUpload, metadataMap)).thenReturn(processedFile);

        ProcessedFile result = mp4FileProcessor.process(fileUpload);
        assertEquals(processedFile, result);
    }


    @Test
    public void shouldThrowFileUploadExceptionOnIOException() throws IOException {
        MultipartFile multipartFile = mock(MultipartFile.class);
        FileUpload fileUpload = mock(FileUpload.class);
        when(fileUpload.getFile()).thenReturn(multipartFile);

        IOException ioException = mock(IOException.class);
        doThrow(ioException).when(multipartFile).getInputStream();

        try {
            mp4FileProcessor.process(fileUpload);
            fail("Expected FileUploadException");
        } catch (FileUploadException e) {
            assertEquals(ioException, e.getCause());
        }
    }
}