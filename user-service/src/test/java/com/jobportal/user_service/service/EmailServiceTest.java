package com.jobportal.user_service.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Test
    void sendEmail_shouldSendMailWithCorrectValues() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        EmailService service = new EmailService(mailSender);

        service.sendEmail("test@mail.com", "Subject", "Body");

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        assertArrayEquals(new String[]{"test@mail.com"}, message.getTo());
        assertEquals("Subject", message.getSubject());
        assertEquals("Body", message.getText());
    }
}
