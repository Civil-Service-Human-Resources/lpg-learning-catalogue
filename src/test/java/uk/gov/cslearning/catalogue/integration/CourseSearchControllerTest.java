package uk.gov.cslearning.catalogue.integration;

import org.junit.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CourseSearchControllerTest extends IntegrationTestBase {

    // Basic

    @Test
    public void testSearchBlank() throws Exception {

        submitRequest(get("/search")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.length()").value(10))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalResults").value(12))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    public void testSearchBasicQuery() throws Exception {

        submitRequest(get("/search")
                        .with(csrf())
                        .param("query", "Course"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.length()").value(10))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalResults").value(11))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    public void testSearchBasicPage() throws Exception {

        submitRequest(get("/search")
                .with(csrf())
                .param("query", "Course")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.length()").value(1))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.totalResults").value(11))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    public void testSearchBasicSize() throws Exception {

        submitRequest(get("/search")
                .with(csrf())
                .param("query", "Course")
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results.length()").value(11))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalResults").value(11))
                .andExpect(jsonPath("$.size").value(20));
    }

    @Test
    public void testSearchDepartments() throws Exception {

        submitRequest(get("/search")
                .with(csrf())
                .param("departments", "COD")
                .param("query", "Course"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalResults").value(3))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    public void testSearchAreasOfWork() throws Exception {

        submitRequest(get("/search")
                .with(csrf())
                .param("areasOfWork", "Analysis")
                .param("query", "Course"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalResults").value(2))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    public void testSearchInterests() throws Exception {

        submitRequest(get("/search")
                .with(csrf())
                .param("interests", "EU")
                .param("query", "Course"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalResults").value(2))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    public void testSearchMultiple() throws Exception {

        submitRequest(get("/search")
                .with(csrf())
                .param("departments", "COD")
                .param("areasOfWork", "Finance")
                .param("query", "Course"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalResults").value(1))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    public void testSearchFree() throws Exception {

        submitRequest(get("/search")
                .with(csrf())
                .param("cost", "free")
                .param("query", "Course"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalResults").value(9))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    public void testSearchModuleTypes() throws Exception {

        submitRequest(get("/search")
                .with(csrf())
                .param("types", "elearning")
                .param("types", "file")
                .param("query", "Course"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalResults").value(5))
                .andExpect(jsonPath("$.size").value(10));
    }

    // Admin

    @Test
    @WithMockUser(username = "user", authorities = {"CSL_AUTHOR"})
    public void testSearchAdmin() throws Exception {

        submitRequest(get("/search/management/courses")
                .with(csrf())
                .param("query", "Course"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalResults").value(11))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"CSL_AUTHOR"})
    public void testSearchAdminArchived() throws Exception {

        submitRequest(get("/search/management/courses")
                .with(csrf())
                .param("query", "Course")
                .param("status", "ARCHIVED")
                .param("status", "PUBLISHED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalResults").value(13))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"CSL_AUTHOR"})
    public void testSearchAdminPrivate() throws Exception {

        submitRequest(get("/search/management/courses")
                .with(csrf())
                .param("query", "Course")
                .param("visibility", "PRIVATE")
                .param("visibility", "PUBLIC"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalResults").value(12))
                .andExpect(jsonPath("$.size").value(10));
    }

    @Test
    @WithMockUser(username = "user", authorities = {"KPMG_SUPPLIER_AUTHOR"})
    public void testSearchAdminKpmgSupplierAuthor() throws Exception {

        submitRequest(get("/search/management/courses")
                .with(csrf())
                .param("query", "Course"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.totalResults").value(1))
                .andExpect(jsonPath("$.size").value(10));
    }

}
