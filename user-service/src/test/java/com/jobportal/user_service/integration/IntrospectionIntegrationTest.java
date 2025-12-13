package com.jobportal.user_service.integration;

import com.jobportal.user_service.entity.UserCredential;
import com.jobportal.user_service.enums.RoleName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.profiles.active=test")
@AutoConfigureMockMvc
@Transactional
@Import(TestBeansConfig.class)
public class IntrospectionIntegrationTest extends IntegrationTestBase {

    @Autowired MockMvc mockMvc;

    @Test
    void introspect_active_true() throws Exception {
        UserCredential uc = createCredential("a@example.com", "Abcd@1234", RoleName.USER, true);

        mockMvc.perform(get("/api/users/introspect")
                        .header("X-User-Id", String.valueOf(uc.getUserId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void introspect_inactive_false() throws Exception {
        UserCredential uc = createCredential("inactive@example.com", "Abcd@1234", RoleName.USER, false);

        mockMvc.perform(get("/api/users/introspect")
                        .header("X-User-Id", String.valueOf(uc.getUserId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false))
                .andExpect(jsonPath("$.error").value("user_not_found_or_disabled"));
    }

    @Test
    void introspect_invalidHeader_returns500() throws Exception {
        mockMvc.perform(get("/api/users/introspect")
                        .header("X-User-Id", "abc"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.active").value(false));
    }
}
