package uk.gov.cslearning.catalogue.config;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import org.apache.tika.Tika;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CloudBlobClient.class, CloudStorageAccount.class})
public class UploadConfigTest {

    private final UploadConfig config = new UploadConfig();

    @Test
    public void storageClientShouldReturnCloudBlobClient() {
        CloudStorageAccount cloudStorageAccount = PowerMockito.mock(CloudStorageAccount.class);
        CloudBlobClient cloudBlobClient = PowerMockito.mock(CloudBlobClient.class);

        PowerMockito.when(cloudStorageAccount.createCloudBlobClient()).thenReturn(cloudBlobClient);

        assertEquals(cloudBlobClient, config.storageClient(cloudStorageAccount));
    }


    @Test
    public void tikaShouldReturnTika() {
        Tika tika = config.tika();
        assertNotNull(tika);
    }

}
