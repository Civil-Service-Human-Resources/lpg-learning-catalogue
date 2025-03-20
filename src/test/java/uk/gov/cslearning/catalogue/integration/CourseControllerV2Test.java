package uk.gov.cslearning.catalogue.integration;

import org.junit.Test;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class CourseControllerV2Test extends IntegrationTestBase {

    @Test
    public void testGetRequiredLearningDepartmentMap() throws Exception {
        submitRequest(get("/v2/courses/required-learning-map")
                .with(csrf()))
                .andExpect(jsonPath("$.departmentCodeMap.HMRC[0]").value("Required course 1"))
                .andExpect(jsonPath("$.departmentCodeMap.HMRC[1]").value("Required course 2"))
                .andExpect(jsonPath("$.departmentCodeMap.CO[0]").value("Required course 1"));
    }

}
