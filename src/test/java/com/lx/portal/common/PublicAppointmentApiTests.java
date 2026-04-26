package com.lx.portal.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.lx.portal.appointment.AppointmentRepository;
import com.lx.portal.appointment.AppointmentStatus;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PublicAppointmentApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @BeforeEach
    void setUp() {
        appointmentRepository.deleteAll();
    }

    @Test
    void createAppointmentAllowsAnonymousSubmissionAndPersistsAppointment() throws Exception {
        mockMvc.perform(post("/api/public/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "nickname": "Test User",
                          "contact": "test@example.com",
                          "city": "Shanghai",
                          "ageRange": "26-35",
                          "consultationTarget": "self",
                          "concernDirection": "stress",
                          "consultationMethod": "online",
                          "preferredTime": "weekday evening",
                          "acceptsRecommendation": true,
                          "problemSummary": "Need a basic consultation.",
                          "privacyAgreed": true,
                          "emergencyAcknowledged": true
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data.id").isNumber())
                .andExpect(jsonPath("$.data.successUrl").value("/appointment-success"));

        assertThat(appointmentRepository.findAll())
                .singleElement()
                .satisfies(appointment -> {
                    assertThat(appointment.getNickname()).isEqualTo("Test User");
                    assertThat(appointment.getContact()).isEqualTo("test@example.com");
                    assertThat(appointment.getStatus()).isEqualTo(AppointmentStatus.PENDING_CONTACT);
                    assertThat(appointment.isPrivacyAgreed()).isTrue();
                    assertThat(appointment.isEmergencyAcknowledged()).isTrue();
                });
    }
}
