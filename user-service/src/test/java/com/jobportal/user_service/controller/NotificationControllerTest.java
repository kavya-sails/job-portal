package com.jobportal.user_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.user_service.dto.JobStatusNotificationRequest;
import com.jobportal.user_service.service.EmailService;
import com.jobportal.user_service.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(NotificationController.class)

class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

//    @Autowired
//    private ObjectMapper objectMapper;

    @MockitoBean //  ADD THIS
    private EmailService emailService;

    @Test
    void testSendJobStatusNotification_Accepted() throws Exception {

        //  Arrange
        JobStatusNotificationRequest request = new JobStatusNotificationRequest();
        request.setUserId(1L);
        request.setJobId(5001L);
        request.setStatus("ACCEPTED");

        Mockito.when(notificationService.sendJobStatusNotification(Mockito.any()))
                .thenReturn("Notification email sent to test@gmail.com");

        //  Act & Assert
        mockMvc.perform(post("/api/notifications/job-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification email sent to test@gmail.com"));
    }
}
