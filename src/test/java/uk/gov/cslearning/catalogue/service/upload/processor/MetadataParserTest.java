package uk.gov.cslearning.catalogue.service.upload.processor;

import com.google.common.collect.ImmutableMap;
import org.apache.tika.Tika;
import org.apache.tika.metadata.Metadata;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.catalogue.exception.FileUploadException;
import uk.gov.cslearning.catalogue.service.upload.processor.metadata.MetadataFactory;
import uk.gov.cslearning.catalogue.service.upload.processor.metadata.MetadataParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MetadataParserTest {
    @Mock
    private Tika tika;
    @Mock
    private MetadataFactory metadataFactory;

    @InjectMocks
    private MetadataParser metadataParser;

    @Test
    public void parseShouldReturnMetadata() throws IOException {
        Metadata metadata = mock(Metadata.class);
        InputStream inputStream = mock(InputStream.class);

        when(metadataFactory.getMetadata()).thenReturn(metadata);

        doAnswer(
                invocation -> {
                    when(metadata.names()).thenReturn(new String[]{"key1", "key2"});
                    when(metadata.get("key1")).thenReturn("value1");
                    when(metadata.get("key2")).thenReturn("value2");
                    return null;
                }).when(tika).parse(inputStream, metadata);

        Map<String, String> expected = ImmutableMap.of(
            "key1", "value1",
            "key2", "value2"
        );

        assertEquals(expected, metadataParser.parse(inputStream));
    }

    @Test(expected = FileUploadException.class)
    public void shouldThrowFileUploadExceptionOnIOException() throws IOException {

        Metadata metadata = mock(Metadata.class);
        InputStream inputStream = mock(InputStream.class);

        IOException ioException = mock(IOException.class);

        when(metadataFactory.getMetadata()).thenReturn(metadata);
        doThrow(ioException).when(tika).parse(inputStream, metadata);
        metadataParser.parse(inputStream);

    }

    @Test
    public void getContentTypeShouldReturnContentTypeString() throws IOException {
        InputStream inputStream = mock(InputStream.class);
        String filename = "file.txt";
        String contentType = "text/plain";

        when(tika.detect(inputStream, filename)).thenReturn(contentType);

        assertEquals(contentType, metadataParser.getContentType(inputStream, filename));
    }

    @Test
    public void getContentTypeShouldCatchIOExceptionAndThrowFileUploadException() throws IOException {
        InputStream inputStream = mock(InputStream.class);
        String filename = "file.txt";
        IOException ioException = mock(IOException.class);

        doThrow(ioException).when(tika).detect(inputStream, filename);

        try {
            metadataParser.getContentType(inputStream, filename);
            fail("Expected FileUploadException");
        }
        catch (Exception e) {
            assertEquals(ioException, e.getCause());
        }
    }
}
