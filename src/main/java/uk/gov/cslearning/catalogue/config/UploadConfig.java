package uk.gov.cslearning.catalogue.config;

import com.google.common.collect.ImmutableMap;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import org.apache.tika.Tika;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.cslearning.catalogue.service.upload.processor.DefaultFileProcessor;
import uk.gov.cslearning.catalogue.service.upload.processor.FileProcessor;
import uk.gov.cslearning.catalogue.service.upload.processor.Mp4FileProcessor;
import uk.gov.cslearning.catalogue.service.upload.processor.ScormFileProcessor;
import uk.gov.cslearning.catalogue.service.upload.uploader.DefaultUploader;
import uk.gov.cslearning.catalogue.service.upload.uploader.ScormUploader;
import uk.gov.cslearning.catalogue.service.upload.uploader.Uploader;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Map;

@Configuration
public class UploadConfig {
    @Bean
    public CloudBlobClient storageClient(CloudStorageAccount cloudStorageAccount) {
        return cloudStorageAccount.createCloudBlobClient();
    }

    @Bean(name = "uploaderMap")
    public Map<String, Uploader> uploaderMap(
            DefaultUploader defaultUploader,
            ScormUploader scormUploader
    ) {
        return ImmutableMap.<String, Uploader>builder()
                .put("doc",  defaultUploader) // MS Word
                .put("docx", defaultUploader) // MS Word
                .put("pdf",  defaultUploader) // PDF
                .put("ppsm", defaultUploader) // MS PowerPoint
                .put("ppt",  defaultUploader) // MS PowerPoint
                .put("pptx", defaultUploader) // MS PowerPoint
                .put("xls",  defaultUploader) // MS Excel
                .put("xlsx", defaultUploader) // MS Excel
                .put("mp4",  defaultUploader) // Video
                .put("zip",  scormUploader)   // Scorm
                .put("jpg", defaultUploader)  //jpeg
                .put("jpeg", defaultUploader) //jpeg
                .put("png", defaultUploader)  //png
                .put("svg", defaultUploader)  //svg
                .build();

    }

    @Bean("fileProcessorMap")
    public Map<String, FileProcessor> fileProcessorMap(
            DefaultFileProcessor defaultFileProcessor,
            Mp4FileProcessor mp4FileProcessor,
            ScormFileProcessor scormFileProcessor
    ) {
        return ImmutableMap.<String, FileProcessor>builder()
                .put("doc",  defaultFileProcessor) // MS Word
                .put("docx", defaultFileProcessor) // MS Word
                .put("pdf",  defaultFileProcessor) // PDF
                .put("ppsm", defaultFileProcessor) // MS PowerPoint
                .put("ppt",  defaultFileProcessor) // MS PowerPoint
                .put("pptx", defaultFileProcessor) // MS PowerPoint
                .put("xls",  defaultFileProcessor) // MS Excel
                .put("xlsx", defaultFileProcessor) // MS Excel
                .put("zip", scormFileProcessor) // Scorm
                .put("mp4",  mp4FileProcessor)     // Video
                .build();
    }

    @Bean(name = "imageProcessorMap")
    public Map<String, FileProcessor> imageProcessorMap(DefaultFileProcessor defaultFileProcessor){
        return ImmutableMap.<String, FileProcessor>builder()
                .put("jpg", defaultFileProcessor)
                .put("jpeg", defaultFileProcessor)
                .put("png", defaultFileProcessor)
                .put("svg", defaultFileProcessor)
                .build();
    }

    @Bean
    public Tika tika() {
        return new Tika();
    }

    @Bean
    public DocumentBuilderFactory documentBuilderFactory() {
        return DocumentBuilderFactory.newInstance();
    }

}
