package uk.gov.cslearning.catalogue.api;

import com.google.gson.Gson;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import springfox.documentation.swagger.readers.operation.ResponseHeaders;
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
@WithMockUser()
public class LearningProviderControllerTest {

    public static final String ID = "abc123";
    public static final String NAME = "New Learning Provider";
    public static final String LEARNING_PROVIDER_CONTROLLER_PATH = "/learning-provider/";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LearningProviderRepository learningProviderRepository;

    private Gson gson = new Gson();

    private LearningProvider createLearningProvider() {
        return new LearningProvider(NAME);
    }

    @Test
    public void shouldCreateLearningProviderAndRedirectToNewPolicy() throws Exception {
        LearningProvider learningProvider = createLearningProvider();

        when(learningProviderRepository.save(any()))
                .thenReturn(learningProvider);

        mockMvc.perform(
                post(LEARNING_PROVIDER_CONTROLLER_PATH).with(csrf())
                        .content(gson.toJson(learningProvider))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "http://localhost/learning-provider/" + learningProvider.getId()));
    }

    @Test
    public void shouldGetLearningProviderIfExists() throws Exception {
        LearningProvider learningProvider = createLearningProvider();

        when(learningProviderRepository.findById(ID))
                .thenReturn(Optional.of(learningProvider));

        mockMvc.perform(
                get(LEARNING_PROVIDER_CONTROLLER_PATH + ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(NAME)));
    }

    @Test
    public void shouldReturnNotFoundIfLearningProviderDoesNotExist() throws Exception {
        when(learningProviderRepository.findById(ID))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                get(LEARNING_PROVIDER_CONTROLLER_PATH + ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldUpdateLearningProvider() throws Exception {
        LearningProvider learningProvider = createLearningProvider();

        when(learningProviderRepository.existsById(learningProvider.getId())).thenReturn(true);
        when(learningProviderRepository.save(any())).thenReturn(learningProvider);

        mockMvc.perform(
                put(LEARNING_PROVIDER_CONTROLLER_PATH + learningProvider.getId()).with(csrf())
                        .content(gson.toJson(learningProvider))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldSendBadRequestIfLearningProviderDoesntExistWhenUpdating() throws Exception {
        LearningProvider learningProvider = createLearningProvider();
        when(learningProviderRepository.existsById(learningProvider.getId())).thenReturn(false);

        mockMvc.perform(
                put(LEARNING_PROVIDER_CONTROLLER_PATH + learningProvider.getId()).with(csrf())
                        .content(gson.toJson(learningProvider))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldDeleteLearningProvider() throws Exception {
        LearningProvider learningProvider = createLearningProvider();
        when(learningProviderRepository.existsById(learningProvider.getId())).thenReturn(true);

        mockMvc.perform(
                post(LEARNING_PROVIDER_CONTROLLER_PATH + learningProvider.getId()).with(csrf())
                        .content(gson.toJson(learningProvider))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldSendBadRequestIfLearningProviderDoesntExistWhenDeleting() throws Exception {
        LearningProvider learningProvider = createLearningProvider();
        when(learningProviderRepository.existsById(learningProvider.getId())).thenReturn(false);

        mockMvc.perform(
                post(LEARNING_PROVIDER_CONTROLLER_PATH + learningProvider.getId()).with(csrf())
                        .content(gson.toJson(learningProvider))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldListLearningProviders() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);

        List<LearningProvider> learningProvidersList = new ArrayList<>();
        learningProvidersList.add(new LearningProvider(NAME + " 1"));
        learningProvidersList.add(new LearningProvider(NAME + " 2"));

        Page<LearningProvider> learningProviders = new PageImpl<>(learningProvidersList);

        when(learningProviderRepository.findAll(pageable))
                .thenReturn(learningProviders);

        mockMvc.perform(
                get(LEARNING_PROVIDER_CONTROLLER_PATH + "list")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results", hasSize(2)))
                .andExpect(jsonPath("$.results[*].name", containsInAnyOrder("New Learning Provider 1", "New Learning Provider 2")));
    }

    @Test
    public void shouldCreateNewCancellationPolicy() throws Exception {
        LearningProvider learningProvider = createLearningProvider();

        when(learningProviderRepository.existsById(learningProvider.getId())).thenReturn(true);

        when(learningProviderRepository.save(any())).thenReturn(learningProvider);

        Optional<LearningProvider> result = Optional.of(learningProvider);

        when(learningProviderRepository.findById(any())).thenReturn(result);

        mockMvc.perform(
                post(LEARNING_PROVIDER_CONTROLLER_PATH + learningProvider.getId() + "/cancellation-policies").with(csrf())
                    .content(gson.toJson(learningProvider))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    /*
    @Test
    public void shouldCreateLearningProviderAndRedirectToNewPolicy() throws Exception {
        LearningProvider learningProvider = createLearningProvider();

        when(learningProviderRepository.save(any()))
                .thenReturn(learningProvider);

        mockMvc.perform(
                post(LEARNING_PROVIDER_CONTROLLER_PATH).with(csrf())
                        .content(gson.toJson(learningProvider))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "http://localhost/learning-provider/" + learningProvider.getId()));
    }
     */
}
