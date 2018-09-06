package uk.gov.cslearning.catalogue.service.upload.client;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class UploadClientFactoryTest {

    @Mock
    private UploadClient uploadClient;

    @InjectMocks
    private UploadClientFactory uploadClientFactory;


    @Test
    public void shouldReturnUploadClient() {
        ProcessedFile processedFile = mock(ProcessedFile.class);
        assertNotNull(uploadClientFactory.create(processedFile));
    }
}