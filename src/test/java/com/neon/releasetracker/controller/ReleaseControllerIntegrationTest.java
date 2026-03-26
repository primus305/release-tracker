package com.neon.releasetracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neon.releasetracker.AbstractReleaseTrackerTest;
import com.neon.releasetracker.config.TestConfig;
import com.neon.releasetracker.data.ReleaseTestData;
import com.neon.releasetracker.model.ReleaseStatus;
import com.neon.releasetracker.request.CreateReleaseRequest;
import com.neon.releasetracker.request.UpdateReleaseRequest;
import com.neon.releasetracker.response.ReleaseResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
@TestPropertySource(properties = {
        "spring.messages.basename=messages/messages"
})
class ReleaseControllerIntegrationTest extends AbstractReleaseTrackerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void givenValidRequest_whenCreate_thenPersistAndReturn201() throws Exception {
        CreateReleaseRequest request = ReleaseTestData.createReleaseRequest();

        mockMvc.perform(post("/releases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(request.name()))
                .andExpect(jsonPath("$.description").value(request.description()))
                .andExpect(jsonPath("$.status").value(ReleaseStatus.CREATED.name()));
    }

    @Test
    void givenInvalidRequest_whenCreate_thenReturn400() throws Exception {
        CreateReleaseRequest invalidRequest = CreateReleaseRequest.builder()
                .build();

        mockMvc.perform(post("/releases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenExistingId_whenUpdate_thenReturnUpdated() throws Exception {
        CreateReleaseRequest createRequest = ReleaseTestData.createReleaseRequest();

        String response = mockMvc.perform(post("/releases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ReleaseResponse created = objectMapper.readValue(response, ReleaseResponse.class);

        UpdateReleaseRequest updateRequest = ReleaseTestData.updateReleaseRequest();

        mockMvc.perform(put("/releases/{id}", created.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.id()))
                .andExpect(jsonPath("$.name").value(updateRequest.name()))
                .andExpect(jsonPath("$.description").value(updateRequest.description()))
                .andExpect(jsonPath("$.status").value(updateRequest.status().name()));
    }

    @Test
    void givenInvalidRequest_whenUpdate_thenReturn400() throws Exception {
        Long id = 1L;
        UpdateReleaseRequest invalidRequest = UpdateReleaseRequest.builder()
                .build();

        mockMvc.perform(put("/releases/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void givenNonExistingId_whenUpdate_thenReturn404() throws Exception {
        Long id = 1L;
        UpdateReleaseRequest request = ReleaseTestData.updateReleaseRequest();

        mockMvc.perform(put("/releases/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void givenExistingId_whenGetById_thenReturn200() throws Exception {
        CreateReleaseRequest request = ReleaseTestData.createReleaseRequest();

        String response = mockMvc.perform(post("/releases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ReleaseResponse created = objectMapper.readValue(response, ReleaseResponse.class);

        mockMvc.perform(get("/releases/{id}", created.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(created.id()))
                .andExpect(jsonPath("$.name").value(created.name()))
                .andExpect(jsonPath("$.description").value(created.description()))
                .andExpect(jsonPath("$.status").value(created.status().name()));
    }

    @Test
    void givenNonExistingId_whenGetById_thenReturn404() throws Exception {
        mockMvc.perform(get("/releases/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void givenExistingId_whenDeleteById_thenReturn204() throws Exception {
        CreateReleaseRequest request = ReleaseTestData.createReleaseRequest();

        String response = mockMvc.perform(post("/releases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ReleaseResponse created = objectMapper.readValue(response, ReleaseResponse.class);

        mockMvc.perform(delete("/releases/{id}", created.id()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/releases/{id}", created.id()))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenNonExistingId_whenDelete_thenReturn404() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/releases/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }
}