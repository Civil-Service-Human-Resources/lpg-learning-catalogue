package uk.gov.cslearning.catalogue.service.upload.processor;


import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.cslearning.catalogue.dto.upload.FileUpload;
import uk.gov.cslearning.catalogue.dto.upload.UploadableFile;
import uk.gov.cslearning.catalogue.service.upload.UploadableFileFactory;
import uk.gov.cslearning.catalogue.service.upload.processor.metadata.MetadataParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class Mp4FileProcessorTest {

    @Mock
    private MetadataParser metadataParser;

    @Mock
    private UploadableFileFactory uploadableFileFactory;

    @InjectMocks
    private Mp4FileProcessor mp4FileProcessor;

    @Test
    public void processShouldSetMetadata() throws IOException {
        Map<String, String> metadataMap = ImmutableMap.of(
                "key1", "value1",
                "key2", ""
        );
        FileUpload fileUpload = mock(FileUpload.class);
        UploadableFile uploadableFile = mock(UploadableFile.class);
        when(uploadableFileFactory.createFromFileUpload(fileUpload)).thenReturn(uploadableFile);

        MultipartFile multipartFile = mock(MultipartFile.class);
        InputStream inputStream = mock(InputStream.class);
        when(multipartFile.getInputStream()).thenReturn(inputStream);
        when(fileUpload.getFile()).thenReturn(multipartFile);

        when(metadataParser.parse(inputStream)).thenReturn(metadataMap);

        Map<String, String> result = mp4FileProcessor.process(fileUpload).getMetadata();
        assertEquals(metadataMap, result);
    }
}
