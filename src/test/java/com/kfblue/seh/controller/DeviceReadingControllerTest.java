package com.kfblue.seh.controller;

import com.kfblue.seh.SmartEnergyHubApplicationTests;
import com.kfblue.seh.common.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDateTime;

@AutoConfigureMockMvc
class DeviceReadingControllerTest extends SmartEnergyHubApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getWaterUsageTrend() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/device-readings/water-usage-trend")
                .param("deviceId", "1")
                .param("startTime", LocalDateTime.now().minusDays(1).toString())
                .param("endTime", LocalDateTime.now().toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200));
    }

    @Test
    void getAlertThreshold() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/device-readings/alert-threshold")
                .param("deviceId", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200));
    }

    @Test
    void getElectricityComparison() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/device-readings/electricity-comparison")
                .param("deviceId", "1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200));
    }
}