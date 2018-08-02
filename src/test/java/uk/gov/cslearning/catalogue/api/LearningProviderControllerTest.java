package uk.gov.cslearning.catalogue.api;

import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.cslearning.catalogue.domain.LearningProvider;
import uk.gov.cslearning.catalogue.repository.LearningProviderRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(LearningProviderController.class)
@WithMockUser(username = "user", password = "password")
public class LearningProviderControllerTest {

    public static final String ID = "abc123";
    public static final String EMAIL = "test@example.com";
    public static final String NAME = "New Learning Provider";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LearningProviderRepository learningProviderRepository;

    private Gson gson = new Gson();

    @Test
    public void shouldCreateLearningProviderAndRedirectToNewProvider() throws Exception {
        LearningProvider learningProvider = createLearningProvider();

        when(learningProviderRepository.save(any()))
                .thenAnswer((Answer<LearningProvider>) invocation -> {
                    learningProvider.setId(ID);
                    return learningProvider;
                });

        mockMvc.perform(
                post("/learning-provider").with(csrf())
                        .content(gson.toJson(learningProvider))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "http://localhost/learning-provider/" + ID));
    }

    private LearningProvider createLearningProvider() {
        return new LearningProvider(NAME,
                EMAIL);
    }
}
