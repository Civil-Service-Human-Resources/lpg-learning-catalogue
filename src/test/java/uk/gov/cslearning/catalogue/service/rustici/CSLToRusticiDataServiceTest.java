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
        String testModuleID = "testModuleID";
        String testCourseID = "testCourseID";
        CreateCourse createCourse = cslToRusticiDataService.getCreateCourseData(testCourseID, testModuleID);
        assertEquals("test.com/testCourseID.testModuleID/imsmanifest.xml", createCourse.getReferenceRequest().getWebPathToCourse());
        assertEquals("test.com/testCourseID.testModuleID", createCourse.getReferenceRequest().getUrl());
    }

}
