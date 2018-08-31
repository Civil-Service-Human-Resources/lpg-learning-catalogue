package uk.gov.cslearning.catalogue.dto;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class ProcessedFileTest {
    @Test
    public void shouldSetProperties() {
        Map<String, String> metadata = ImmutableMap.of("key", "value");

        FileUpload fileUpload = mock(FileUpload.class);

        ProcessedFile processedFile = new ProcessedFile(fileUpload);
        processedFile.setMetadata(metadata);

        assertEquals(fileUpload, processedFile.getFileUpload());
        assertEquals(metadata, processedFile.getMetadata());
        assertTrue(processedFile.getTimestamp().isBefore(LocalDateTime.now(Clock.systemUTC()).plusSeconds(1)));
        assertTrue(processedFile.getTimestamp().isAfter(LocalDateTime.now(Clock.systemUTC()).minusSeconds(5)));

        String pattern = "uk.gov.cslearning.catalogue.dto.ProcessedFile@(\\w+)" +
                "\\[fileUpload=Mock for FileUpload, hashCode: (\\d+),metadata=\\{key=value\\}," +
                "timestamp=(\\d\\d\\d\\d\\-\\d\\d\\-\\d\\dT\\d\\d\\:\\d\\d\\:\\d\\d\\.\\d\\d\\d)\\]";

        assertTrue(Pattern.matches(pattern, processedFile.toString()));
    }
}