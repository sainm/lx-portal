package com.lx.portal.appointment;

import java.time.LocalDateTime;

public record AppointmentDetailDto(
        Long id,
        String nickname,
        String contact,
        String city,
        String ageRange,
        String consultationTarget,
        String concernDirection,
        String consultationMethod,
        String preferredTime,
        Long preferredCounselorId,
        boolean acceptsRecommendation,
        String problemSummary,
        boolean privacyAgreed,
        boolean emergencyAcknowledged,
        AppointmentStatus status,
        String internalNote,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static AppointmentDetailDto from(Appointment appointment) {
        return new AppointmentDetailDto(
                appointment.getId(),
                appointment.getNickname(),
                appointment.getContact(),
                appointment.getCity(),
                appointment.getAgeRange(),
                appointment.getConsultationTarget(),
                appointment.getConcernDirection(),
                appointment.getConsultationMethod(),
                appointment.getPreferredTime(),
                appointment.getPreferredCounselor() == null ? null : appointment.getPreferredCounselor().getId(),
                appointment.isAcceptsRecommendation(),
                appointment.getProblemSummary(),
                appointment.isPrivacyAgreed(),
                appointment.isEmergencyAcknowledged(),
                appointment.getStatus(),
                appointment.getInternalNote(),
                appointment.getCreatedAt(),
                appointment.getUpdatedAt());
    }
}

