package uk.gov.cslearning.catalogue.config;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import org.apache.tika.Tika;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UploadConfigTest {

    private final UploadConfig config = new UploadConfig();

    @Test
    public void cloudStorageAccountShouldReturnCloudStorageAccount() {
        String connectionString = "UseDevelopmentStorage=true";
        CloudStorageAccount account = config.cloudStorageAccount(connectionString);
        assertNotNull(account);
    }

    @Test
    public void storageClientShouldReturnCloudBlobClient() {
        CloudStorageAccount cloudStorageAccount = Mockito.mock(CloudStorageAccount.class);
        CloudBlobClient cloudBlobClient = Mockito.mock(CloudBlobClient.class);

        Mockito.when(cloudStorageAccount.createCloudBlobClient()).thenReturn(cloudBlobClient);

        assertEquals(cloudBlobClient, config.storageClient(cloudStorageAccount));
    }


    @Test
    public void tikaShouldReturnTika() {
        Tika tika = config.tika();
        assertNotNull(tika);
    }

}
