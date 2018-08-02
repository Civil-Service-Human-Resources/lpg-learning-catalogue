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
import uk.gov.cslearning.catalogue.domain.TermsAndConditions;
import uk.gov.cslearning.catalogue.repository.TermsAndConditionsRepository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(TermsAndConditionsController.class)
@WithMockUser(username = "user", password = "password")
public class TermsAndConditionsControllerTest {

    public static final String ID = "abc123";
    public static final String NAME = "New Terms";
    public static final String DESCRIPTION = "New description";
    public static final String EMAIL = "test@example.com";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TermsAndConditionsRepository termsAndConditionsRepository;

    private Gson gson = new Gson();

    @Test
    public void shouldCreateLearningProviderAndRedirectToNewResource() throws Exception {
        TermsAndConditions termsAndConditions = createTermsAndConditions();

        when(termsAndConditionsRepository.save(any()))
                .thenAnswer((Answer<TermsAndConditions>) invocation -> {
                    termsAndConditions.setId(ID);
                    return termsAndConditions;
                });

        mockMvc.perform(
                post("/terms-and-conditions").with(csrf())
                        .content(gson.toJson(termsAndConditions))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "http://localhost/terms-and-conditions/" + ID));
    }

    private TermsAndConditions createTermsAndConditions() {
        return new TermsAndConditions(NAME, DESCRIPTION, EMAIL);
    }
}
