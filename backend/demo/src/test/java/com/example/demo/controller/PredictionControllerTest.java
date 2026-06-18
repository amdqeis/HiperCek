package com.example.demo.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import com.example.demo.repository.InMemoryPredictionHistoryRepository;
import com.example.demo.service.CardiovascularAIService;
import com.example.demo.service.HypertensionAIService;
import com.example.demo.service.PredictionService;

class PredictionControllerTest {
    private MockMvc mockMvc;

    private RestTemplate restTemplate;

    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        restTemplate = new RestTemplate();
        server = MockRestServiceServer.createServer(restTemplate);
        PredictionService predictionService = new PredictionService(
            new HypertensionAIService(restTemplate, "http://127.0.0.1:8000"),
            new CardiovascularAIService(restTemplate, "http://127.0.0.1:8000"),
            new InMemoryPredictionHistoryRepository()
        );
        mockMvc = MockMvcBuilders.standaloneSetup(new PredictionController(predictionService))
            .setControllerAdvice(new ApiExceptionHandler())
            .build();
    }

    @Test
    void createPredictionStoresHistory() throws Exception {
        mockMlSuccessResponses();

        mockMvc.perform(post("/api/predictions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validPayload()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.hypertension.category", is("high")))
            .andExpect(jsonPath("$.cardiovascular.category", is("low")));

        mockMvc.perform(get("/api/predictions/history"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].hypertension.percentage", is(75.0)));
    }

    @Test
    void deleteHistoryRemovesSavedItem() throws Exception {
        mockMlSuccessResponses();

        String response = mockMvc.perform(post("/api/predictions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validPayload()))
            .andReturn()
            .getResponse()
            .getContentAsString();

        String id = response.split("\"id\":\"")[1].split("\"")[0];

        mockMvc.perform(delete("/api/predictions/history/{id}", id))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/predictions/history"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void createPredictionRejectsInvalidBloodPressure() throws Exception {
        mockMvc.perform(post("/api/predictions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "age": 45,
                      "bmi": 27.4,
                      "systolicBp": 120,
                      "diastolicBp": 120,
                      "familyHistory": true,
                      "smokingStatus": "Former",
                      "physicalActivityLevel": "Moderate",
                      "diabetes": false
                    }
                    """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", is("Tekanan diastolik harus lebih kecil dari sistolik.")));
    }

    @Test
    void createPredictionReturnsBadGatewayWhenMlFails() throws Exception {
        server.expect(MockRestRequestMatchers.requestTo("http://127.0.0.1:8000/predict"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withServerError());

        mockMvc.perform(post("/api/predictions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validPayload()))
            .andExpect(status().isBadGateway())
            .andExpect(jsonPath("$.message", is("Gagal menghubungi ML API.")));
    }

    private void mockMlSuccessResponses() {
        server.expect(MockRestRequestMatchers.requestTo("http://127.0.0.1:8000/predict"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withSuccess("75.0", MediaType.APPLICATION_JSON));

        server.expect(MockRestRequestMatchers.requestTo("http://127.0.0.1:8000/predict/cardiovascular"))
            .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
            .andRespond(MockRestResponseCreators.withSuccess("18.0", MediaType.APPLICATION_JSON));
    }

    private String validPayload() {
        return """
            {
              "age": 50,
              "bmi": 26.5,
              "systolicBp": 145,
              "diastolicBp": 92,
              "familyHistory": true,
              "smokingStatus": "Former",
              "physicalActivityLevel": "Low",
              "diabetes": false
            }
            """;
    }
}
