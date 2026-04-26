package com.lx.portal.appointment;

import java.time.LocalDateTime;

public record AppointmentListDto(
        Long id,
        String nickname,
        String contact,
        String city,
        String concernDirection,
        String consultationMethod,
        String preferredTime,
        AppointmentStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static AppointmentListDto from(Appointment appointment) {
        return new AppointmentListDto(
                appointment.getId(),
                appointment.getNickname(),
                appointment.getContact(),
                appointment.getCity(),
                appointment.getConcernDirection(),
                appointment.getConsultationMethod(),
                appointment.getPreferredTime(),
                appointment.getStatus(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt());
    }
}

