package com.lx.portal.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.lx.portal.appointment.Appointment;

@Service
public class NotificationService {
    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final String webhookUrl;
    private final String baseUrl;
    private final RestClient restClient;

    public NotificationService(@Value("${app.notification.webhook-url:}") String webhookUrl,
            @Value("${app.base-url:http://127.0.0.1:8080}") String baseUrl) {
        this.webhookUrl = webhookUrl;
        this.baseUrl = baseUrl;
        this.restClient = RestClient.create();
    }

    public void notifyAppointment(Appointment appointment) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.info("New appointment submitted: id={}, concern={}, method={}",
                    appointment.getId(), appointment.getConcernDirection(), appointment.getConsultationMethod());
            return;
        }
        try {
            String text = "新预约线索：%s，方向：%s，方式：%s，偏好时间：%s。后台详情：%s/admin/appointments"
                    .formatted(appointment.getNickname(), appointment.getConcernDirection(),
                            appointment.getConsultationMethod(), appointment.getPreferredTime(), baseUrl);
            restClient.post().uri(webhookUrl).body(new WebhookText(text)).retrieve().toBodilessEntity();
        } catch (RuntimeException ex) {
            log.warn("Failed to send appointment notification", ex);
        }
    }

    record WebhookText(String text) {
    }
}
