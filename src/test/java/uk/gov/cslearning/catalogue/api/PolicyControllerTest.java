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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.cslearning.catalogue.domain.Policy;
import uk.gov.cslearning.catalogue.repository.PolicyRepository;

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
@WebMvcTest(PolicyController.class)
@WithMockUser()
public class PolicyControllerTest {

    public static final String ID = "abc123";
    public static final String NAME = "New Terms and Conditions";
    public static final String DESCRIPTION = "Example description";
    public static final String TERMS_AND_CONDITIONS_CONTROLLER_PATH = "/terms-and-conditions/";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PolicyRepository policyRepository;

    private Gson gson = new Gson();

    private Policy createTermsAndConditions() {
        return new Policy(NAME, DESCRIPTION);
    }

    @Test
    public void shouldCreateTermsAndConditionsAndRedirectToNewPolicy() throws Exception {
        Policy policy = createTermsAndConditions();

        when(policyRepository.save(any()))
                .thenReturn(policy);

        mockMvc.perform(
                post(TERMS_AND_CONDITIONS_CONTROLLER_PATH).with(csrf())
                        .content(gson.toJson(policy))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "http://localhost" + TERMS_AND_CONDITIONS_CONTROLLER_PATH + policy.getId()));
    }

    @Test
    public void shouldGetTermsAndConditionsIfExists() throws Exception {
        Policy policy = createTermsAndConditions();

        when(policyRepository.findById(ID))
                .thenReturn(Optional.of(policy));

        mockMvc.perform(
                get(TERMS_AND_CONDITIONS_CONTROLLER_PATH + ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(NAME)))
                .andExpect(jsonPath("$.description", is(DESCRIPTION)));
    }

    @Test
    public void shouldReturnNotFoundIfTermsAndConditionsDoesNotExist() throws Exception {
        when(policyRepository.findById(ID))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                get(TERMS_AND_CONDITIONS_CONTROLLER_PATH + ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldUpdateTermsAndConditions() throws Exception {
        Policy policy = createTermsAndConditions();
        when(policyRepository.existsById(policy.getId())).thenReturn(true);
        when(policyRepository.save(any())).thenReturn(policy);

        mockMvc.perform(
                put(TERMS_AND_CONDITIONS_CONTROLLER_PATH + policy.getId()).with(csrf())
                        .content(gson.toJson(policy))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldSendBadRequestIfTermsAndConditionsDoesntExistWhenUpdating() throws Exception {
        Policy policy = createTermsAndConditions();
        when(policyRepository.existsById(policy.getId())).thenReturn(false);

        mockMvc.perform(
                put(TERMS_AND_CONDITIONS_CONTROLLER_PATH + policy.getId()).with(csrf())
                        .content(gson.toJson(policy))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldDeleteTermsAndConditions() throws Exception {
        Policy policy = createTermsAndConditions();
        when(policyRepository.existsById(policy.getId())).thenReturn(true);

        mockMvc.perform(
                post(TERMS_AND_CONDITIONS_CONTROLLER_PATH + policy.getId()).with(csrf())
                        .content(gson.toJson(policy))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldSendBadRequestIfTermsAndConditionsDoesntExistWhenDeleting() throws Exception {
        Policy policy = createTermsAndConditions();
        when(policyRepository.existsById(policy.getId())).thenReturn(false);

        mockMvc.perform(
                post(TERMS_AND_CONDITIONS_CONTROLLER_PATH + policy.getId()).with(csrf())
                        .content(gson.toJson(policy))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldListCancellationPolicies() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);

        List<Policy> policyList = new ArrayList<>();
        policyList.add(new Policy(NAME + " 1", DESCRIPTION));
        policyList.add(new Policy(NAME + " 2", DESCRIPTION));

        Page<Policy> cancellationPolicies = new PageImpl<>(policyList);

        when(policyRepository.findAll(pageable))
                .thenReturn(cancellationPolicies);

        mockMvc.perform(
                get(TERMS_AND_CONDITIONS_CONTROLLER_PATH + "list")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results", hasSize(2)))
                .andExpect(jsonPath("$.results[*].name", containsInAnyOrder(NAME + " 1", NAME + " 2")))
                .andExpect(jsonPath("$.results[0].description", is("Example description")));
    }

}
