package uk.gov.cslearning.catalogue.service.upload.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ELearningManifestService {

    /**
     * This list should be given in order of preference - with the preferred manifest being at position 0.
     * This means that if a package has multiple manifest files, we can predictably pick a preferred one to use
     * for the import.
     */
    private final List<String> validManifests;

    public ELearningManifestService(@Qualifier("elearning_manifest_list") List<String> validManifests) {
        this.validManifests = validManifests;
    }

    public String fetchManifestFromFileList(List<String> filenames) {
        List<String> availableManifests = validManifests.stream().filter(filenames::contains).collect(Collectors.toList());
        if (availableManifests.isEmpty()) {
            return null;
        }
        if (availableManifests.size() > 1) {
            String joinedAvailableManfiests = String.join(",", availableManifests);
            String preferredDefaultManifest = availableManifests.get(0);
            log.info(String.format("Files contain more than one valid manifest file (%s). Picking the preferred default manifest: %s", joinedAvailableManfiests, preferredDefaultManifest));
            return preferredDefaultManifest;
        }
        return availableManifests.get(0);
    }
}
