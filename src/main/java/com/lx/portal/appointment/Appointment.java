package com.lx.portal.appointment;

import com.lx.portal.common.BaseEntity;
import com.lx.portal.counselor.Counselor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Appointment extends BaseEntity {
    @Column(nullable = false)
    private String nickname;
    @Column(nullable = false)
    private String contact;
    private String city;
    private String ageRange;
    private String consultationTarget;
    private String concernDirection;
    private String consultationMethod;
    private String preferredTime;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preferred_counselor_id")
    private Counselor preferredCounselor;
    @Column(nullable = false)
    private boolean acceptsRecommendation = true;
    @Column(columnDefinition = "text")
    private String problemSummary;
    @Column(nullable = false)
    private boolean privacyAgreed;
    @Column(nullable = false)
    private boolean emergencyAcknowledged;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status = AppointmentStatus.PENDING_CONTACT;
    @Column(columnDefinition = "text")
    private String internalNote;

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getAgeRange() { return ageRange; }
    public void setAgeRange(String ageRange) { this.ageRange = ageRange; }
    public String getConsultationTarget() { return consultationTarget; }
    public void setConsultationTarget(String consultationTarget) { this.consultationTarget = consultationTarget; }
    public String getConcernDirection() { return concernDirection; }
    public void setConcernDirection(String concernDirection) { this.concernDirection = concernDirection; }
    public String getConsultationMethod() { return consultationMethod; }
    public void setConsultationMethod(String consultationMethod) { this.consultationMethod = consultationMethod; }
    public String getPreferredTime() { return preferredTime; }
    public void setPreferredTime(String preferredTime) { this.preferredTime = preferredTime; }
    public Counselor getPreferredCounselor() { return preferredCounselor; }
    public void setPreferredCounselor(Counselor preferredCounselor) { this.preferredCounselor = preferredCounselor; }
    public boolean isAcceptsRecommendation() { return acceptsRecommendation; }
    public void setAcceptsRecommendation(boolean acceptsRecommendation) { this.acceptsRecommendation = acceptsRecommendation; }
    public String getProblemSummary() { return problemSummary; }
    public void setProblemSummary(String problemSummary) { this.problemSummary = problemSummary; }
    public boolean isPrivacyAgreed() { return privacyAgreed; }
    public void setPrivacyAgreed(boolean privacyAgreed) { this.privacyAgreed = privacyAgreed; }
    public boolean isEmergencyAcknowledged() { return emergencyAcknowledged; }
    public void setEmergencyAcknowledged(boolean emergencyAcknowledged) { this.emergencyAcknowledged = emergencyAcknowledged; }
    public AppointmentStatus getStatus() { return status; }
    public void setStatus(AppointmentStatus status) { this.status = status; }
    public String getInternalNote() { return internalNote; }
    public void setInternalNote(String internalNote) { this.internalNote = internalNote; }
}

