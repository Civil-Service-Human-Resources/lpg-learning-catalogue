package uk.gov.cslearning.catalogue.service.upload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.cslearning.catalogue.config.FileUploadMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FileUploadServiceFactory {

    private final FileUploadMap fileUploadMap;

    private final Map<UploadServiceType, FileUploadService> fileUploadServiceMap;

    @Autowired
    private FileUploadServiceFactory(FileUploadMap fileUploadMap, List<FileUploadService> fileUploadServices) {
        this.fileUploadMap = fileUploadMap;
        fileUploadServiceMap = fileUploadServices.stream().collect(Collectors.toMap(FileUploadService::getType, Function.identity()));
    }

    public FileUploadService getFileUploadService(UploadServiceType type) {
        return Optional.ofNullable(fileUploadServiceMap.get(type)).orElseThrow(IllegalArgumentException::new);
    }

    public FileUploadService getFileUploadServiceWithExt(String ext) {
        UploadServiceType type = Optional.ofNullable(this.fileUploadMap.getServiceTypeFromExt(ext)).orElseThrow(IllegalArgumentException::new);
        return this.getFileUploadService(type);
    }
}
