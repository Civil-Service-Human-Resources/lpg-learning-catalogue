package uk.gov.cslearning.catalogue.service.rustici;

import org.junit.Before;
import org.junit.Test;
import uk.gov.cslearning.catalogue.dto.rustici.course.CreateCourse;

import static org.junit.Assert.assertEquals;

public class CSLToRusticiDataServiceTest {

    private CSLToRusticiDataService cslToRusticiDataService;

    @Before
    public void beforeEach() {
        cslToRusticiDataService = new CSLToRusticiDataService("test.com");
    }

    @Test
    public void testGetCreateCourseData() {
        String testMediaId = "testMediaID";
        String testCourseID = "testCourseID";
        String testManifestFile = "testManifest.xml";
        CreateCourse createCourse = cslToRusticiDataService.getCreateCourseData(testCourseID, testMediaId, testManifestFile);
        assertEquals("test.com/testCourseID/testMediaID/testManifest.xml", createCourse.getReferenceRequest().getUrl());
        assertEquals("test.com/testCourseID/testMediaID", createCourse.getReferenceRequest().getWebPathToCourse());
    }

}
