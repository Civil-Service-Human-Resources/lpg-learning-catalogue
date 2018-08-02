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
import uk.gov.cslearning.catalogue.domain.CancellationPolicy;
import uk.gov.cslearning.catalogue.repository.CancellationPolicyRepository;

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
@WebMvcTest(CancellationPolicyController.class)
@WithMockUser(username = "user", password = "password")
public class CancellationPolicyControllerTest {

    public static final String ID = "abc123";
    public static final String NAME = "New Cancellation Policy";
    public static final String SHORT_VERSION = "Short version";
    public static final String FULL_VERSION = "Full version";
    public static final String EMAIL = "test@example.com";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CancellationPolicyRepository cancellationPolicyRepository;

    private Gson gson = new Gson();

    private CancellationPolicy createCancellationPolicy() {
        return new CancellationPolicy(NAME, SHORT_VERSION, FULL_VERSION, EMAIL);
    }

    @Test
    public void shouldCreateCancellationPolicyAndRedirectToNewPolicy() throws Exception {
        CancellationPolicy cancellationPolicy = createCancellationPolicy();

        when(cancellationPolicyRepository.save(any()))
                .thenAnswer((Answer<CancellationPolicy>) invocation -> {
                    cancellationPolicy.setId(ID);
                    return cancellationPolicy;
                });

        mockMvc.perform(
                post("/cancellation-policy").with(csrf())
                        .content(gson.toJson(cancellationPolicy))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "http://localhost/cancellation-policy/" + ID));
    }

    @Test
    public void shouldGetCancellationPolicy() throws Exception {
        List<String> ids = new ArrayList<>();
        ids.add(ID);

        CancellationPolicy cancellationPolicy = createCancellationPolicy();

        when(cancellationPolicyRepository.findById(ID))
                .thenReturn(Optional.of(cancellationPolicy));

        mockMvc.perform(
                get("/cancellation-policy/" + ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(NAME)))
                .andExpect(jsonPath("$.shortVersion", is(SHORT_VERSION)))
                .andExpect(jsonPath("$.createdByEmail", is(EMAIL)))
                .andExpect(jsonPath("$.fullVersion", is(FULL_VERSION)));
    }

    @Test
    public void shouldUpdateCancellationPolicy() throws Exception {
        CancellationPolicy cancellationPolicy = createCancellationPolicy();
        when(cancellationPolicyRepository.existsById(cancellationPolicy.getId())).thenReturn(true);
        when(cancellationPolicyRepository.save(any())).thenReturn(cancellationPolicy);

        mockMvc.perform(
                put("/cancellation-policy/" + cancellationPolicy.getId()).with(csrf())
                        .content(gson.toJson(cancellationPolicy))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldSendBadRequestIfCancellationPolicyDoesntExistWhenUpdating() throws Exception {
        CancellationPolicy cancellationPolicy = createCancellationPolicy();
        when(cancellationPolicyRepository.existsById(cancellationPolicy.getId())).thenReturn(false);

        mockMvc.perform(
                put("/cancellation-policy/" + cancellationPolicy.getId()).with(csrf())
                        .content(gson.toJson(cancellationPolicy))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldDeleteCancellationPolicy() throws Exception {
        CancellationPolicy cancellationPolicy = createCancellationPolicy();
        when(cancellationPolicyRepository.existsById(cancellationPolicy.getId())).thenReturn(true);

        mockMvc.perform(
                post("/cancellation-policy/" + cancellationPolicy.getId()).with(csrf())
                        .content(gson.toJson(cancellationPolicy))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldSendBadRequestIfCancellationPolicyDoesntExistWhenDeleting() throws Exception {
        CancellationPolicy cancellationPolicy = createCancellationPolicy();
        when(cancellationPolicyRepository.existsById(cancellationPolicy.getId())).thenReturn(false);

        mockMvc.perform(
                post("/cancellation-policy/" + cancellationPolicy.getId()).with(csrf())
                        .content(gson.toJson(cancellationPolicy))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldListCancellationPolicies() throws Exception {
        Pageable pageable = PageRequest.of(0, 10);

        List<CancellationPolicy> cancellationPoliciesList = new ArrayList<>();
        cancellationPoliciesList.add(new CancellationPolicy(NAME + "1", SHORT_VERSION, FULL_VERSION, EMAIL));
        cancellationPoliciesList.add(new CancellationPolicy(NAME + "2", SHORT_VERSION, FULL_VERSION, EMAIL));

        Page<CancellationPolicy> cancellationPolicies = new PageImpl<>(cancellationPoliciesList);

        when(cancellationPolicyRepository.findAll(pageable))
                .thenReturn(cancellationPolicies);

        mockMvc.perform(
                get("/cancellation-policy")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.results", hasSize(2)))
                .andExpect(jsonPath("$.results[*].name", containsInAnyOrder("New Cancellation Policy1", "New Cancellation Policy2")))
                .andExpect(jsonPath("$.results[0].shortVersion", is("Short version")))
                .andExpect(jsonPath("$.results[0].fullVersion", is("Full version")))
                .andExpect(jsonPath("$.results[0].createdByEmail", is("test@example.com")));
    }

}
