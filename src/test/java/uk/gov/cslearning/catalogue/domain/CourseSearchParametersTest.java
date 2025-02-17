package uk.gov.cslearning.catalogue.domain;

import org.junit.Test;
import uk.gov.cslearning.catalogue.api.v2.model.CourseSearchParameters;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


public class CourseSearchParametersTest {
    @Test
    public void testHasAudienceFieldReturnsFalseIfNoneOfAudienceFieldsIsPopulated() {
        boolean expectedResult = false;
        CourseSearchParameters parameters = new CourseSearchParameters();
        parameters.setQuery("corruption");

        boolean actualResult = parameters.hasAudienceFields();

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testHasAudienceFieldReturnsTrueIfDepartmentsIsPopulated() {
        boolean expectedResult = true;
        CourseSearchParameters parameters = new CourseSearchParameters();
        parameters.setQuery("corruption");
        List<String> departments = new ArrayList<>();
        departments.add("co");
        parameters.setDepartments(departments);

        boolean actualResult = parameters.hasAudienceFields();

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testCostIsFreeReturnsTrueIfCostStringIsFree() {
        boolean expectedResult = true;
        CourseSearchParameters parameters = new CourseSearchParameters();
        parameters.setCost("free");

        boolean actualResult = parameters.costIsFree();

        assertEquals(expectedResult, actualResult);
    }

    @Test
    public void testCostIsFreeReturnsFalseIfCostNotSpecified() {
        boolean expectedResult = false;
        CourseSearchParameters parameters = new CourseSearchParameters();

        boolean actualResult = parameters.costIsFree();

        assertEquals(expectedResult, actualResult);
    }
}
