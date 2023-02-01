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
        CreateCourse createCourse = cslToRusticiDataService.getCreateCourseData(testCourseID, testMediaId);
        assertEquals("test.com/testCourseID/testMediaID/imsmanifest.xml", createCourse.getReferenceRequest().getUrl());
        assertEquals("test.com/testCourseID/testMediaID", createCourse.getReferenceRequest().getWebPathToCourse());
    }

}
