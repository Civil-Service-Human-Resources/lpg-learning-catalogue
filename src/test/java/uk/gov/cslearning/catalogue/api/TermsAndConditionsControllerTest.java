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
@WithMockUser(username = "user", password = "password")
public class TermsAndConditionsControllerTest {

    public static final String ID = "abc123";
    public static final String NAME = "New Terms and Conditions";
    public static final String DESCRIPTION = "Example description";
    public static final String EMAIL = "test@example.com";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TermsAndConditionsRepository termsAndConditionsRepository;

    private Gson gson = new Gson();

    private TermsAndConditions createTermsAndConditions() {
        return new TermsAndConditions(NAME, DESCRIPTION, EMAIL);
    }

    @Test
    public void shouldCreateTermsAndConditionsAndRedirectToNewPolicy() throws Exception {
        TermsAndConditions TermsAndConditions = createTermsAndConditions();

        when(termsAndConditionsRepository.save(any()))
                .thenAnswer((Answer<TermsAndConditions>) invocation -> {
                    TermsAndConditions.setId(ID);
                    return TermsAndConditions;
                });

        mockMvc.perform(
                post("/terms-and-conditions").with(csrf())
                        .content(gson.toJson(TermsAndConditions))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "http://localhost/terms-and-conditions/" + ID));
    }

    @Test
    public void shouldGetTermsAndConditions() throws Exception {
        List<String> ids = new ArrayList<>();
        ids.add(ID);

        TermsAndConditions TermsAndConditions = createTermsAndConditions();

        when(termsAndConditionsRepository.findById(ID))
                .thenReturn(Optional.of(TermsAndConditions));

        mockMvc.perform(
                get("/terms-and-conditions/" + ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(NAME)))
                .andExpect(jsonPath("$.createdByEmail", is(EMAIL)))
                .andExpect(jsonPath("$.description", is(DESCRIPTION)));
    }

    @Test
    public void shouldUpdateTermsAndConditions() throws Exception {
        TermsAndConditions TermsAndConditions = createTermsAndConditions();
        when(termsAndConditionsRepository.existsById(TermsAndConditions.getId())).thenReturn(true);
        when(termsAndConditionsRepository.save(any())).thenReturn(TermsAndConditions);

        mockMvc.perform(
                put("/terms-and-conditions/" + TermsAndConditions.getId()).with(csrf())
                        .content(gson.toJson(TermsAndConditions))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldSendBadRequestIfTermsAndConditionsDoesntExistWhenUpdating() throws Exception {
        TermsAndConditions TermsAndConditions = createTermsAndConditions();
        when(termsAndConditionsRepository.existsById(TermsAndConditions.getId())).thenReturn(false);

        mockMvc.perform(
                put("/terms-and-conditions/" + TermsAndConditions.getId()).with(csrf())
                        .content(gson.toJson(TermsAndConditions))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldDeleteTermsAndConditions() throws Exception {
        TermsAndConditions TermsAndConditions = createTermsAndConditions();
        when(termsAndConditionsRepository.existsById(TermsAndConditions.getId())).thenReturn(true);

        mockMvc.perform(
                post("/terms-and-conditions/" + TermsAndConditions.getId()).with(csrf())
                        .content(gson.toJson(TermsAndConditions))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldListCancellationPolicies() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);

        List<TermsAndConditions> termsAndConditionsList = new ArrayList<>();
        termsAndConditionsList.add(new TermsAndConditions(NAME + "1", DESCRIPTION, EMAIL));
        termsAndConditionsList.add(new TermsAndConditions(NAME + "2", DESCRIPTION, EMAIL));

        Page<TermsAndConditions> cancellationPolicies = new PageImpl<>(termsAndConditionsList);

        when(termsAndConditionsRepository.findAll(pageable))
                .thenReturn(cancellationPolicies);

        mockMvc.perform(
                get("/terms-and-conditions")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results", hasSize(2)))
                .andExpect(jsonPath("$.results[*].name", containsInAnyOrder(NAME + "1", NAME + "2")))
                .andExpect(jsonPath("$.results[0].description", is("Example description")))
                .andExpect(jsonPath("$.results[0].createdByEmail", is("test@example.com")));
    }

}
