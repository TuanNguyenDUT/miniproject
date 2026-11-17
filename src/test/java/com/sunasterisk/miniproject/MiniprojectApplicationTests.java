package com.sunasterisk.miniproject;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class MiniprojectApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void userCanViewEmployeesAndStatisticsButCannotCreate() throws Exception {
        mockMvc.perform(get("/api/employees").with(httpBasic("user", "user123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());

        mockMvc.perform(get("/api/employees/statistics").with(httpBasic("user", "user123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEmployees", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.departmentStatistics").isArray());

        mockMvc.perform(get("/employees/statistics").with(httpBasic("user", "user123")))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Employees by Department")));

        String newEmployeeJson = objectMapper.writeValueAsString(Map.of(
                "name", "Test User",
                "email", "blocked@sun-asterisk.vn",
                "position", "QA",
                "salary", 15000000
        ));

        mockMvc.perform(post("/api/employees")
                        .with(httpBasic("user", "user123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newEmployeeJson))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanCreateEmployeeAndGetCachedCount() throws Exception {
        String newEmployeeJson = objectMapper.writeValueAsString(Map.of(
                "name", "Le Minh Tuan",
                "email", "tuan.le@sun-asterisk.vn",
                "phone", "0909999888",
                "position", "Software Engineer",
                "salary", 25000000
        ));

        mockMvc.perform(post("/api/employees")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(newEmployeeJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("tuan.le@sun-asterisk.vn"));

        mockMvc.perform(get("/api/employees/count").with(httpBasic("user", "user123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEmployees", greaterThanOrEqualTo(6)));
    }

    @Test
    void registerAndLoginWithJwtWorks() throws Exception {
        String registerJson = objectMapper.writeValueAsString(Map.of(
                "username", "newstudent",
                "password", "secret123",
                "role", "USER"
        ));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("newstudent"));

        String loginJson = objectMapper.writeValueAsString(Map.of(
                "username", "newstudent",
                "password", "secret123"
        ));

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn();

        JsonNode node = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        String token = node.get("token").asText();

        mockMvc.perform(get("/api/employees")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
