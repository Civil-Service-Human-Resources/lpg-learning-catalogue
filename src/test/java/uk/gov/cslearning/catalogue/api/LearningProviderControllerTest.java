package uk.gov.cslearning.catalogue.api;

import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.cslearning.catalogue.domain.LearningProvider;
import uk.gov.cslearning.catalogue.repository.LearningProviderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(LearningProviderController.class)
@WithMockUser(username = "user", password = "password")
public class LearningProviderControllerTest {

    public static final String ID = "abc123";
    public static final String NAME = "New Learning Provider";
    public static final String EMAIL = "test@example.com";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LearningProviderRepository learningProviderRepository;

    private Gson gson = new Gson();

    private LearningProvider createLearningProvider() {
        return new LearningProvider(NAME, EMAIL);
    }

    @Test
    public void shouldCreateLearningProviderAndRedirectToNewPolicy() throws Exception {
        LearningProvider LearningProvider = createLearningProvider();

        when(learningProviderRepository.save(any()))
                .thenAnswer((Answer<LearningProvider>) invocation -> {
                    LearningProvider.setId(ID);
                    return LearningProvider;
                });

        mockMvc.perform(
                post("/learning-provider").with(csrf())
                        .content(gson.toJson(LearningProvider))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "http://localhost/learning-provider/" + ID));
    }

    @Test
    public void shouldGetLearningProvider() throws Exception {
        List<String> ids = new ArrayList<>();
        ids.add(ID);

        LearningProvider LearningProvider = createLearningProvider();

        when(learningProviderRepository.findById(ID))
                .thenReturn(Optional.of(LearningProvider));

        mockMvc.perform(
                get("/learning-provider/" + ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(NAME)))
                .andExpect(jsonPath("$.createdByEmail", is(EMAIL)));
    }

    @Test
    public void shouldUpdateLearningProvider() throws Exception {
        LearningProvider LearningProvider = createLearningProvider();
        when(learningProviderRepository.existsById(LearningProvider.getId())).thenReturn(true);
        when(learningProviderRepository.save(any())).thenReturn(LearningProvider);

        mockMvc.perform(
                put("/learning-provider/" + LearningProvider.getId()).with(csrf())
                        .content(gson.toJson(LearningProvider))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldSendBadRequestIfLearningProviderDoesntExistWhenUpdating() throws Exception {
        LearningProvider LearningProvider = createLearningProvider();
        when(learningProviderRepository.existsById(LearningProvider.getId())).thenReturn(false);

        mockMvc.perform(
                put("/learning-provider/" + LearningProvider.getId()).with(csrf())
                        .content(gson.toJson(LearningProvider))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldDeleteLearningProvider() throws Exception {
        LearningProvider LearningProvider = createLearningProvider();
        when(learningProviderRepository.existsById(LearningProvider.getId())).thenReturn(true);

        mockMvc.perform(
                post("/learning-provider/" + LearningProvider.getId()).with(csrf())
                        .content(gson.toJson(LearningProvider))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldListLearningProivders() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);

        List<LearningProvider> learningProviderList = new ArrayList<>();
        learningProviderList.add(new LearningProvider(NAME + "1", EMAIL));
        learningProviderList.add(new LearningProvider(NAME + "2", EMAIL));

        Page<LearningProvider> cancellationPolicies = new PageImpl<>(learningProviderList);

        when(learningProviderRepository.findAll(pageable))
                .thenReturn(cancellationPolicies);

        mockMvc.perform(
                get("/learning-provider")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results", hasSize(2)))
                .andExpect(jsonPath("$.results[*].name", containsInAnyOrder(NAME + "1", NAME + "2")))
                .andExpect(jsonPath("$.results[0].createdByEmail", is("test@example.com")));
    }

}
