package uk.gov.cslearning.catalogue.service.upload.processor;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class ELearningManifestServiceTest {

    private final List<String> validManifestFiles = Arrays.asList("imsmanifest.xml", "testmanifest.xml");
    private ELearningManifestService eLearningManifestService;

    @Before
    public void before() {
        this.eLearningManifestService = new ELearningManifestService(validManifestFiles);
    }

    @Test
    public void testOrderingOfDefaultManfiest() {
        String resultingManfiest = this.eLearningManifestService.fetchManifestFromFileList(Arrays.asList("testmanifest.xml", "imsmanifest.xml"));
        assertEquals("Manifest files do not match", resultingManfiest, "imsmanifest.xml");
    }

    @Test
    public void testManifestIsNullIfNotFound() {
        String resultingManfiest = this.eLearningManifestService.fetchManifestFromFileList(Collections.singletonList("notfoundmanifest.xml"));
        assertNull("Manifest should be null", resultingManfiest);
    }
}
