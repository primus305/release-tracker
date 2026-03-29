package com.neon.releasetracker.security;

import com.neon.releasetracker.AbstractReleaseTrackerTest;
import com.neon.releasetracker.config.TestJacksonConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestJacksonConfig.class)
@TestPropertySource(properties = {
        "spring.messages.basename=messages/messages",
        "release-tracker.security.enabled=true"
})
public class ReleaseTrackerSecurityIntegrationTest extends AbstractReleaseTrackerTest {
    private static final String TRACE_HEADER = "X-Trace-ID";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Test
    void givenNoToken_whenGetReleases_thenReturn401() throws Exception {
        mockMvc.perform(get("/v1/releases"))
                .andExpect(status().isUnauthorized())
                .andExpect(header().exists(TRACE_HEADER))
                .andExpect(header().string(TRACE_HEADER, notNullValue()));
    }

    @Test
    void givenInvalidToken_whenGetReleases_thenReturn401() throws Exception {
        mockMvc.perform(get("/v1/releases")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer abc.def.ghi"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void givenNoToken_whenAccessSwagger_thenReturn200() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void givenValidToken_whenGetReleases_thenReturn200() throws Exception {
        mockMvc.perform(get("/v1/releases")
                        .with(jwtUser("user")))
                .andExpect(header().exists(TRACE_HEADER))
                .andExpect(header().string(TRACE_HEADER, notNullValue()))
                .andExpect(status().isOk());
    }

    private RequestPostProcessor jwtUser(String username) {
        String token = jwtTokenService.generateToken(username);
        return request -> {
            request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
            return request;
        };
    }
}
