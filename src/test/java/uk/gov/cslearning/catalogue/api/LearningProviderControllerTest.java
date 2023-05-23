package uk.gov.cslearning.catalogue.api;

import com.google.gson.Gson;
import org.glassfish.jersey.servlet.WebConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.cslearning.catalogue.domain.CancellationPolicy;
import uk.gov.cslearning.catalogue.domain.LearningProvider;
import uk.gov.cslearning.catalogue.domain.TermsAndConditions;
import uk.gov.cslearning.catalogue.repository.LearningProviderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(LearningProviderController.class)
@WithMockUser()
@ContextConfiguration(classes = {WebConfig.class, LearningProviderController.class})
public class LearningProviderControllerTest {

    public static final String ID = "abc123";
    public static final String NAME = "New Learning Provider";
    public static final String LEARNING_PROVIDER_CONTROLLER_PATH = "/learning-providers/";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LearningProviderRepository learningProviderRepository;

    private Gson gson = new Gson();

    private LearningProvider createLearningProvider() {
        return new LearningProvider(NAME);
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
                get(LEARNING_PROVIDER_CONTROLLER_PATH)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results", hasSize(2)))
                .andExpect(jsonPath("$.results[*].name", containsInAnyOrder("New Learning Provider 1", "New Learning Provider 2")));
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
                .andExpect(header().string("location", "http://localhost/learning-providers/" + learningProvider.getId()));
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
                delete(LEARNING_PROVIDER_CONTROLLER_PATH + learningProvider.getId()).with(csrf())
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
                delete(LEARNING_PROVIDER_CONTROLLER_PATH + learningProvider.getId()).with(csrf())
                        .content(gson.toJson(learningProvider))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldAddCancellationPolicyToLearningProvider() throws Exception {
        LearningProvider learningProvider = createLearningProvider();

        when(learningProviderRepository.existsById(learningProvider.getId())).thenReturn(true);

        when(learningProviderRepository.findById(learningProvider.getId())).thenReturn(Optional.of(learningProvider));

        when(learningProviderRepository.save(any())).thenReturn(learningProvider);

        mockMvc.perform(
                post(LEARNING_PROVIDER_CONTROLLER_PATH + learningProvider.getId() + "/cancellation-policies").with(csrf())
                        .content(gson.toJson(learningProvider))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldSendBadRequestWhenCancellationPolicyIsNull() throws Exception {
        LearningProvider learningProvider = createLearningProvider();
        CancellationPolicy cancellationPolicy = null;

        when(learningProviderRepository.existsById(learningProvider.getId())).thenReturn(true);

        when(learningProviderRepository.save(any())).thenReturn(learningProvider);

        Optional<LearningProvider> result = Optional.of(learningProvider);

        when(learningProviderRepository.findById(any())).thenReturn(result);

        mockMvc.perform(
                post(LEARNING_PROVIDER_CONTROLLER_PATH + learningProvider.getId() + "/cancellation-policies").with(csrf())
                        .content(gson.toJson(cancellationPolicy))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldGetCancellationPolicyFromLearningProvider() throws Exception {
        LearningProvider learningProvider = createLearningProvider();
        CancellationPolicy cancellationPolicy = new CancellationPolicy();

        learningProvider.addCancellationPolicy(cancellationPolicy);

        when(learningProviderRepository.existsById(learningProvider.getId())).thenReturn(true);

        Optional<LearningProvider> result = Optional.of(learningProvider);

        when(learningProviderRepository.findById(learningProvider.getId())).thenReturn(result);

        mockMvc.perform(
                get(LEARNING_PROVIDER_CONTROLLER_PATH + learningProvider.getId() + "/cancellation-policies/" + cancellationPolicy.getId()).with(csrf())
                        .content(gson.toJson(cancellationPolicy))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

    }

    @Test
    public void shouldUpdateCancellationPolicyInLearningProvider() throws Exception {
        LearningProvider learningProvider = createLearningProvider();
        CancellationPolicy cancellationPolicy = new CancellationPolicy();
        CancellationPolicy newCancellationPolicy = new CancellationPolicy();

        cancellationPolicy.setName("old");
        newCancellationPolicy.setName("new");

        learningProvider.addCancellationPolicy(cancellationPolicy);

        when(learningProviderRepository.existsById(learningProvider.getId())).thenReturn(true);

        when(learningProviderRepository.save(any())).thenReturn(learningProvider);

        Optional<LearningProvider> result = Optional.of(learningProvider);

        when(learningProviderRepository.findById(learningProvider.getId())).thenReturn(result);

        mockMvc.perform(
                put(LEARNING_PROVIDER_CONTROLLER_PATH + learningProvider.getId() + "/cancellation-policies/" + cancellationPolicy.getId()).with(csrf())
                        .content(gson.toJson(newCancellationPolicy))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        assert (learningProvider.getCancellationPolicies().size() == 1);
        assertEquals(learningProvider.getCancellationPolicies().get(0).getId(), cancellationPolicy.getId());
        assertEquals("new", learningProvider.getCancellationPolicies().get(0).getName());
    }

    @Test
    public void shouldDeleteCancellationPolicyFromLearningProvider() throws Exception {
        LearningProvider learningProvider = createLearningProvider();
        CancellationPolicy cancellationPolicy = new CancellationPolicy();

        learningProvider.addCancellationPolicy(cancellationPolicy);

        when(learningProviderRepository.existsById(learningProvider.getId())).thenReturn(true);

        when(learningProviderRepository.save(any())).thenReturn(learningProvider);

        Optional<LearningProvider> result = Optional.of(learningProvider);

        when(learningProviderRepository.findById(learningProvider.getId())).thenReturn(result);

        mockMvc.perform(
                delete(LEARNING_PROVIDER_CONTROLLER_PATH + learningProvider.getId() + "/cancellation-policies/" + cancellationPolicy.getId()).with(csrf())
                        .content(gson.toJson(cancellationPolicy))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        assert (learningProvider.getCancellationPolicies().isEmpty());
    }

    @Test
    public void shouldGetTermsAndConditionsFromLearningProvider() throws Exception {
        LearningProvider learningProvider = createLearningProvider();
        TermsAndConditions termsAndConditions = new TermsAndConditions();

        learningProvider.addTermsAndConditions(termsAndConditions);

        when(learningProviderRepository.existsById(learningProvider.getId())).thenReturn(true);

        Optional<LearningProvider> result = Optional.of(learningProvider);

        when(learningProviderRepository.findById(learningProvider.getId())).thenReturn(result);

        mockMvc.perform(
                get(LEARNING_PROVIDER_CONTROLLER_PATH + learningProvider.getId() + "/terms-and-conditions/" + termsAndConditions.getId()).with(csrf())
                        .content(gson.toJson(termsAndConditions))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldAddTermsAndConditionsToLearningProvider() throws Exception {
        LearningProvider learningProvider = createLearningProvider();

        when(learningProviderRepository.existsById(learningProvider.getId())).thenReturn(true);

        when(learningProviderRepository.findById(learningProvider.getId())).thenReturn(Optional.of(learningProvider));

        when(learningProviderRepository.save(any())).thenReturn(learningProvider);

        mockMvc.perform(
                post(LEARNING_PROVIDER_CONTROLLER_PATH + learningProvider.getId() + "/terms-and-conditions").with(csrf())
                        .content(gson.toJson(learningProvider))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    public void shouldUpdateTermsAndConditionsInLearningProvider() throws Exception {
        LearningProvider learningProvider = createLearningProvider();
        TermsAndConditions termsAndConditions = new TermsAndConditions();
        TermsAndConditions newTermsAndConditions = new TermsAndConditions();

        termsAndConditions.setName("old");
        newTermsAndConditions.setName("new");

        learningProvider.addTermsAndConditions(termsAndConditions);

        when(learningProviderRepository.existsById(learningProvider.getId())).thenReturn(true);

        when(learningProviderRepository.save(any())).thenReturn(learningProvider);

        Optional<LearningProvider> result = Optional.of(learningProvider);

        when(learningProviderRepository.findById(learningProvider.getId())).thenReturn(result);

        mockMvc.perform(
                put(LEARNING_PROVIDER_CONTROLLER_PATH + learningProvider.getId() + "/terms-and-conditions/" + termsAndConditions.getId()).with(csrf())
                        .content(gson.toJson(newTermsAndConditions))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        assert (learningProvider.getTermsAndConditions().size() == 1);
        assertEquals(termsAndConditions.getId(), learningProvider.getTermsAndConditions().get(0).getId());
        assertEquals("new", learningProvider.getTermsAndConditions().get(0).getName());
    }

    @Test
    public void shouldDeleteTermsAndConditionsFromLearningProvider() throws Exception {
        LearningProvider learningProvider = createLearningProvider();
        TermsAndConditions termsAndConditions = new TermsAndConditions();

        learningProvider.addTermsAndConditions(termsAndConditions);

        when(learningProviderRepository.existsById(learningProvider.getId())).thenReturn(true);

        when(learningProviderRepository.save(any())).thenReturn(learningProvider);

        Optional<LearningProvider> result = Optional.of(learningProvider);

        when(learningProviderRepository.findById(learningProvider.getId())).thenReturn(result);

        mockMvc.perform(
                delete(LEARNING_PROVIDER_CONTROLLER_PATH + learningProvider.getId() + "/terms-and-conditions/" + termsAndConditions.getId()).with(csrf())
                        .content(gson.toJson(termsAndConditions))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());

        assert (learningProvider.getTermsAndConditions().isEmpty());
    }
}
