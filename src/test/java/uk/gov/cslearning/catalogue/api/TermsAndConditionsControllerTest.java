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
import uk.gov.cslearning.catalogue.domain.TermsAndConditions;
import uk.gov.cslearning.catalogue.repository.TermsAndConditionsRepository;

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
@WebMvcTest(TermsAndConditionsController.class)
@WithMockUser()
public class TermsAndConditionsControllerTest {

    public static final String ID = "abc123";
    public static final String NAME = "New Terms and Conditions";
    public static final String DESCRIPTION = "Example description";
    public static final String TERMS_AND_CONDITIONS_CONTROLLER_PATH = "/terms-and-conditions/";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TermsAndConditionsRepository termsAndConditionsRepository;

    private Gson gson = new Gson();

    private TermsAndConditions createTermsAndConditions() {
        return new TermsAndConditions(NAME, DESCRIPTION);
    }

    @Test
    public void shouldCreateTermsAndConditionsAndRedirectToNewPolicy() throws Exception {
        TermsAndConditions termsAndConditions = createTermsAndConditions();

        when(termsAndConditionsRepository.save(any()))
                .thenReturn(termsAndConditions);

        mockMvc.perform(
                post(TERMS_AND_CONDITIONS_CONTROLLER_PATH).with(csrf())
                        .content(gson.toJson(termsAndConditions))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "http://localhost" + TERMS_AND_CONDITIONS_CONTROLLER_PATH + termsAndConditions.getId()));
    }

    @Test
    public void shouldGetTermsAndConditionsIfExists() throws Exception {
        TermsAndConditions termsAndConditions = createTermsAndConditions();

        when(termsAndConditionsRepository.findById(ID))
                .thenReturn(Optional.of(termsAndConditions));

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
        when(termsAndConditionsRepository.findById(ID))
                .thenReturn(Optional.empty());

        mockMvc.perform(
                get(TERMS_AND_CONDITIONS_CONTROLLER_PATH + ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldUpdateTermsAndConditions() throws Exception {
        TermsAndConditions termsAndConditions = createTermsAndConditions();
        when(termsAndConditionsRepository.existsById(termsAndConditions.getId())).thenReturn(true);
        when(termsAndConditionsRepository.save(any())).thenReturn(termsAndConditions);

        mockMvc.perform(
                put(TERMS_AND_CONDITIONS_CONTROLLER_PATH + termsAndConditions.getId()).with(csrf())
                        .content(gson.toJson(termsAndConditions))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldSendBadRequestIfTermsAndConditionsDoesntExistWhenUpdating() throws Exception {
        TermsAndConditions termsAndConditions = createTermsAndConditions();
        when(termsAndConditionsRepository.existsById(termsAndConditions.getId())).thenReturn(false);

        mockMvc.perform(
                put(TERMS_AND_CONDITIONS_CONTROLLER_PATH + termsAndConditions.getId()).with(csrf())
                        .content(gson.toJson(termsAndConditions))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldDeleteTermsAndConditions() throws Exception {
        TermsAndConditions termsAndConditions = createTermsAndConditions();
        when(termsAndConditionsRepository.existsById(termsAndConditions.getId())).thenReturn(true);

        mockMvc.perform(
                post(TERMS_AND_CONDITIONS_CONTROLLER_PATH + termsAndConditions.getId()).with(csrf())
                        .content(gson.toJson(termsAndConditions))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldSendBadRequestIfTermsAndConditionsDoesntExistWhenDeleting() throws Exception {
        TermsAndConditions termsAndConditions = createTermsAndConditions();
        when(termsAndConditionsRepository.existsById(termsAndConditions.getId())).thenReturn(false);

        mockMvc.perform(
                post(TERMS_AND_CONDITIONS_CONTROLLER_PATH + termsAndConditions.getId()).with(csrf())
                        .content(gson.toJson(termsAndConditions))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldListCancellationPolicies() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);

        List<TermsAndConditions> termsAndConditionsList = new ArrayList<>();
        termsAndConditionsList.add(new TermsAndConditions(NAME + " 1", DESCRIPTION));
        termsAndConditionsList.add(new TermsAndConditions(NAME + " 2", DESCRIPTION));

        Page<TermsAndConditions> cancellationPolicies = new PageImpl<>(termsAndConditionsList);

        when(termsAndConditionsRepository.findAll(pageable))
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
