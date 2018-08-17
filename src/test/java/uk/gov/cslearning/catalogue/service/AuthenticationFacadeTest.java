package uk.gov.cslearning.catalogue.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(AuthenticationFacade.class)
@WithMockUser(username = "test-user", password = "password")
public class AuthenticationFacadeTest {

    @Autowired
    private AuthenticationFacade authenticationFacade;

    @Test
    public void getAuthenticationReturnsAuthentication() {
        assertEquals("test-user", authenticationFacade.getAuthentication().getName());
    }
}