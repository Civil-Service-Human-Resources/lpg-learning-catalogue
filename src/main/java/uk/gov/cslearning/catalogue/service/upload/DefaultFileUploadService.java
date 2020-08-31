package uk.gov.cslearning.catalogue.service.upload;

import java.io.IOException;

import org.apache.tomcat.util.http.fileupload.FileUploadBase;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.dto.Upload;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClientFactory;
import uk.gov.cslearning.catalogue.service.upload.processor.FileProcessor;
import uk.gov.cslearning.catalogue.service.upload.processor.FileProcessorFactory;
import uk.gov.cslearning.catalogue.service.upload.processor.ImageProcessorFactory;
import uk.gov.cslearning.catalogue.service.upload.uploader.Uploader;
import uk.gov.cslearning.catalogue.service.upload.uploader.UploaderFactory;

@Service
public class DefaultFileUploadService implements FileUploadService {
    private final FileProcessorFactory fileProcessorFactory;
    private final ImageProcessorFactory imageProcessorFactory;
    private final UploadClientFactory uploadClientFactory;
    private final UploaderFactory uploaderFactory;

    public DefaultFileUploadService(FileProcessorFactory fileProcessorFactory,
                                    ImageProcessorFactory imageProcessorFactory,
                                    UploadClientFactory uploadClientFactory,
                                    UploaderFactory uploaderFactory) {
        this.fileProcessorFactory = fileProcessorFactory;
        this.uploadClientFactory = uploadClientFactory;
        this.uploaderFactory = uploaderFactory;
        this.imageProcessorFactory = imageProcessorFactory;
    }

    @Override
    public Upload upload(FileUpload fileUpload) {

        FileProcessor fileProcessor = fileProcessorFactory.create(fileUpload);
        return getUpload(fileUpload, fileProcessor);
    }

    @Override
    public Upload uploadImageForSkills(FileUpload fileUpload) throws IOException {
        imageProcessorFactory.validateProcessedFile(fileUpload);
        FileProcessor fileProcessor = imageProcessorFactory.create(fileUpload);
        return getUpload(fileUpload, fileProcessor);
    }

    private Upload getUpload(FileUpload fileUpload, FileProcessor fileProcessor) {
        ProcessedFile processedFile = fileProcessor.process(fileUpload);
        UploadClient uploadClient = uploadClientFactory.create(processedFile);
        Uploader uploader = uploaderFactory.create(processedFile);
        return uploader.upload(processedFile, uploadClient);
    }

    @Override
    public void delete(String filePath) {
        UploadClient uploadClient = uploadClientFactory.create(null);

        String items[] = filePath.split("/");

        if (items.length > 3) {
            String relativePath = items[items.length - 3] + "/" + items[items.length - 2] + "/" + items[items.length - 1];
            uploadClient.delete(relativePath);
        }
    }

    @Override
    public void deleteDirectory(String filePath) {
        UploadClient uploadClient = uploadClientFactory.create(null);

        String items[] = filePath.split("/");
        String relativePath = items[items.length - 2] + "/" + items[items.length - 1];

        uploadClient.deleteDirectory(relativePath);
    }
}
