package com.neon.releasetracker.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.neon.releasetracker.config.TestJacksonConfig;
import com.neon.releasetracker.data.ReleaseTestData;
import com.neon.releasetracker.exception.ReleaseNotFoundException;
import com.neon.releasetracker.request.CreateReleaseRequest;
import com.neon.releasetracker.request.UpdateReleaseRequest;
import com.neon.releasetracker.response.ReleaseResponse;
import com.neon.releasetracker.response.SearchResponse;
import com.neon.releasetracker.security.JwtService;
import com.neon.releasetracker.service.ReleaseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReleaseController.class)
@Import(TestJacksonConfig.class)
@TestPropertySource(properties = {
        "spring.messages.basename=messages/messages"
})
class ReleaseControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReleaseService releaseService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void givenValidRequest_whenCreate_thenReturn201() throws Exception {
        CreateReleaseRequest request = ReleaseTestData.createReleaseRequest().build();
        ReleaseResponse expectedResponse = ReleaseTestData.releaseResponse();

        given(releaseService.create(any())).willReturn(expectedResponse);

        mockMvc.perform(post("/releases")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(expectedResponse.id()))
                .andExpect(jsonPath("$.name").value(expectedResponse.name()))
                .andExpect(jsonPath("$.description").value(expectedResponse.description()));
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
    void givenValidRequest_whenUpdate_thenReturn200() throws Exception {
        Long id = 1L;
        UpdateReleaseRequest request = ReleaseTestData.updateReleaseRequest().build();
        ReleaseResponse response = ReleaseTestData.releaseResponse();

        given(releaseService.update(eq(id), any())).willReturn(response);

        mockMvc.perform(put("/releases/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(response.name()));
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
        UpdateReleaseRequest request = ReleaseTestData.updateReleaseRequest().build();

        given(releaseService.update(eq(id), any()))
                .willThrow(new ReleaseNotFoundException(id));

        mockMvc.perform(put("/releases/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void givenValidId_whenGetById_thenReturn200() throws Exception {
        Long id = 1L;
        ReleaseResponse response = ReleaseTestData.releaseResponse();

        given(releaseService.getById(id)).willReturn(response);

        mockMvc.perform(get("/releases/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(response.name()));
    }

    @Test
    void givenInvalidId_whenGetById_thenReturn400() throws Exception {
        mockMvc.perform(get("/releases/-1"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(releaseService);
    }

    @Test
    void givenNonExistingId_whenGetById_thenReturn404() throws Exception {
        Long id = 1L;

        given(releaseService.getById(id))
                .willThrow(new ReleaseNotFoundException(id));

        mockMvc.perform(get("/releases/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void givenExistingId_whenDelete_thenReturn204() throws Exception {
        mockMvc.perform(delete("/releases/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(releaseService).deleteById(1L);
    }

    @Test
    void givenNonExistingId_whenDelete_thenReturn404() throws Exception {
        Long id = 1L;

        willThrow(new ReleaseNotFoundException(id))
                .given(releaseService).deleteById(id);

        mockMvc.perform(delete("/releases/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void givenValidSearchParams_whenSearch_thenReturn200() throws Exception {
        ReleaseResponse releaseResponse = ReleaseTestData.releaseResponse();
        SearchResponse<ReleaseResponse> searchResponse = SearchResponse.<ReleaseResponse>builder()
                .content(List.of(releaseResponse))
                .page(0)
                .size(10)
                .totalElements(1)
                .build();

        given(releaseService.search(any(), any())).willReturn(searchResponse);

        mockMvc.perform(get("/releases")
                        .param("name", "test")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(releaseResponse.id()))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    void givenInvalidDateRange_whenSearch_thenReturn400() throws Exception {
        mockMvc.perform(get("/releases")
                        .param("releaseDateFrom", "2026-12-01")
                        .param("releaseDateTo", "2026-01-01")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(releaseService);
    }
}