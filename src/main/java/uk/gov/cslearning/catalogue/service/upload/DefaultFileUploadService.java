package uk.gov.cslearning.catalogue.service.upload;

import java.io.IOException;

import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.dto.ProcessedFile;
import uk.gov.cslearning.catalogue.dto.Upload;
import uk.gov.cslearning.catalogue.service.upload.client.UploadClient;
import uk.gov.cslearning.catalogue.service.upload.processor.FileProcessor;
import uk.gov.cslearning.catalogue.service.upload.processor.FileProcessorFactory;
import uk.gov.cslearning.catalogue.service.upload.processor.ImageProcessorFactory;
import uk.gov.cslearning.catalogue.service.upload.uploader.Uploader;
import uk.gov.cslearning.catalogue.service.upload.uploader.UploaderFactory;

@Service
public class DefaultFileUploadService implements FileUploadService {
    private final FileProcessorFactory fileProcessorFactory;
    private final ImageProcessorFactory imageProcessorFactory;
    private final UploaderFactory uploaderFactory;
    private final UploadClient uploadClient;

    public DefaultFileUploadService(FileProcessorFactory fileProcessorFactory,
                                    ImageProcessorFactory imageProcessorFactory,
                                    UploaderFactory uploaderFactory,
                                    UploadClient uploadClient) {
        this.fileProcessorFactory = fileProcessorFactory;
        this.uploaderFactory = uploaderFactory;
        this.imageProcessorFactory = imageProcessorFactory;
        this.uploadClient = uploadClient;
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
        Uploader uploader = uploaderFactory.create(processedFile);
        return uploader.upload(processedFile);
    }

    @Override
    public void delete(String filePath) {

        String[] items = filePath.split("/");

        if (items.length > 3) {
            String relativePath = items[items.length - 3] + "/" + items[items.length - 2] + "/" + items[items.length - 1];
            uploadClient.delete(relativePath);
        }
    }

    @Override
    public void deleteDirectory(String filePath) {

        String[] items = filePath.split("/");
        String relativePath = items[items.length - 2] + "/" + items[items.length - 1];

        uploadClient.deleteDirectory(relativePath);
    }
}
