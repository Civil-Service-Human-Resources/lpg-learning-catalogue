package uk.gov.cslearning.catalogue.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.cslearning.catalogue.service.FileUploadFactory;
import uk.gov.cslearning.catalogue.service.MediaManagementService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(MediaController.class)
@WithMockUser(username = "user", password = "password")
public class MediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MediaManagementService mediaManagementService;

    @MockBean
    private FileUploadFactory fileUploadFactory;

    @Test
    public void shouldUploadFileOnPostRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile("test.doc", "".getBytes());

        mockMvc.perform(
                multipart("/service/media")
                        .file(file)
                        .param("container", "container-id")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .accept(MediaType.APPLICATION_JSON).with(csrf()))
                .andExpect(status().isNoContent());
    }
}