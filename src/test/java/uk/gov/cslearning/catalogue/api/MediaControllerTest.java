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
import uk.gov.cslearning.catalogue.domain.Media;
import uk.gov.cslearning.catalogue.dto.FileUpload;
import uk.gov.cslearning.catalogue.service.upload.FileUploadFactory;
import uk.gov.cslearning.catalogue.service.upload.MediaManagementService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(MediaController.class)
@WithMockUser(username = "user")
public class MediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MediaManagementService mediaManagementService;

    @MockBean
    private FileUploadFactory fileUploadFactory;

    @Test
    public void shouldUploadFileOnPostRequest() throws Exception {
        String fileContainer = "container-id";
        String mediaId = "media-uid";
        String filename = "custom-filename";

        MockMultipartFile file = new MockMultipartFile("file", "file.doc", "application/octet-stream", "abc".getBytes());
        FileUpload fileUpload = mock(FileUpload.class);

        when(fileUploadFactory.create(file, fileContainer, filename)).thenReturn(fileUpload);

        Media media = mock(Media.class);
        when(media.getId()).thenReturn(mediaId);
        when(mediaManagementService.create(fileUpload)).thenReturn(media);

        mockMvc.perform(
                multipart("/media")
                        .file(file)
                        .param("container", fileContainer)
                        .param("filename", filename)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON).with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "http://localhost/media/" + mediaId));
    }

    @Test
    public void shouldReturn200OnSuccessfulGetRequest() throws Exception {
        String mediaId = "media-id";

        long filesize = 10;
        String container = "test-container";
        LocalDateTime date = LocalDateTime.now();
        String extension = "doc";
        String id = "media-id";
        String name = "filename";
        String path = "test-path";

        Media media = new Media();
        media.setFileSize(filesize);
        media.setContainer(container);
        media.setDateAdded(date);
        media.setExtension(extension);
        media.setId(id);
        media.setName(name);
        media.setPath(path);

        when(mediaManagementService.findById(mediaId)).thenReturn(Optional.of(media));

        mockMvc.perform(
                get("/media/" + mediaId)
                        .accept(MediaType.APPLICATION_JSON).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.container", is(container)))
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.fileSize", is((int) filesize)))
                .andExpect(jsonPath("$.extension", is(extension)))
                .andExpect(jsonPath("$.name", is(name)))
                .andExpect(jsonPath("$.dateAdded", is(date.format(DateTimeFormatter.ISO_DATE_TIME))))
                .andExpect(jsonPath("$.path", is(path)));
    }

    @Test
    public void shouldReturn404IfMediaNotFound() throws Exception {
        String mediaUid = "media-uid";

        when(mediaManagementService.findById(mediaUid)).thenReturn(Optional.empty());

        mockMvc.perform(
                get("/media/" + mediaUid)
                        .accept(MediaType.APPLICATION_JSON).with(csrf()))
                .andExpect(status().isNotFound());
    }

}