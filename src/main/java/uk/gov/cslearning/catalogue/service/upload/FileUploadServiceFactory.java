package uk.gov.cslearning.catalogue.service.upload;

import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FileUploadServiceFactory {

    Map<String, UploadServiceType> extServiceMap = ImmutableMap.<String, UploadServiceType>builder()
                .put("doc",  UploadServiceType.FILE)
                .put("docx", UploadServiceType.FILE)
                .put("pdf",  UploadServiceType.FILE)
                .put("ppsm", UploadServiceType.FILE)
                .put("ppt",  UploadServiceType.FILE)
                .put("pptx", UploadServiceType.FILE)
                .put("xls",  UploadServiceType.FILE)
                .put("xlsx", UploadServiceType.FILE)
                .put("zip", UploadServiceType.SCORM)
                .put("mp4",  UploadServiceType.MP4)
                .put("jpg", UploadServiceType.IMAGE)
                .put("jpeg", UploadServiceType.IMAGE)
                .put("png", UploadServiceType.IMAGE)
                .put("svg", UploadServiceType.IMAGE)
                .build();

    private final Map<UploadServiceType, FileUploadService> fileUploadServiceMap;

    @Autowired
    private FileUploadServiceFactory(List<FileUploadService> fileUploadServices) {
        fileUploadServiceMap = fileUploadServices.stream().collect(Collectors.toMap(FileUploadService::getType, Function.identity()));
    }

    public List<String> getValidFileExts() {
        return new ArrayList<>(this.extServiceMap.keySet());
    }

    public FileUploadService getFileUploadService(UploadServiceType type) {
        return Optional.ofNullable(fileUploadServiceMap.get(type)).orElseThrow(IllegalArgumentException::new);
    }

    public FileUploadService getFileUploadServiceWithExt(String ext) {
        UploadServiceType type = Optional.ofNullable(this.extServiceMap.get(ext)).orElseThrow(IllegalArgumentException::new);
        return this.getFileUploadService(type);
    }
}
