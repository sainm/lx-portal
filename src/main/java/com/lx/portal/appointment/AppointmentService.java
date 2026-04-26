package com.lx.portal.appointment;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.lx.portal.counselor.CounselorRepository;
import com.lx.portal.notification.NotificationService;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final CounselorRepository counselorRepository;
    private final NotificationService notificationService;

    public AppointmentService(AppointmentRepository appointmentRepository, CounselorRepository counselorRepository,
            NotificationService notificationService) {
        this.appointmentRepository = appointmentRepository;
        this.counselorRepository = counselorRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public Appointment create(AppointmentRequest request) {
        Appointment appointment = new Appointment();
        appointment.setNickname(request.nickname());
        appointment.setContact(request.contact());
        appointment.setCity(request.city());
        appointment.setAgeRange(request.ageRange());
        appointment.setConsultationTarget(request.consultationTarget());
        appointment.setConcernDirection(request.concernDirection());
        appointment.setConsultationMethod(request.consultationMethod());
        appointment.setPreferredTime(request.preferredTime());
        appointment.setAcceptsRecommendation(request.acceptsRecommendation());
        appointment.setProblemSummary(request.problemSummary());
        appointment.setPrivacyAgreed(request.privacyAgreed());
        appointment.setEmergencyAcknowledged(request.emergencyAcknowledged());
        if (request.preferredCounselorId() != null) {
            appointment.setPreferredCounselor(counselorRepository.findById(request.preferredCounselorId()).orElse(null));
        }
        Appointment saved = appointmentRepository.save(appointment);
        notificationService.notifyAppointment(saved);
        return saved;
    }

    @Transactional
    public Appointment updateStatus(Long id, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("预约不存在"));
        appointment.setStatus(status);
        return appointment;
    }

    @Transactional
    public Appointment updateNote(Long id, String note) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("预约不存在"));
        appointment.setInternalNote(note);
        return appointment;
    }
}
