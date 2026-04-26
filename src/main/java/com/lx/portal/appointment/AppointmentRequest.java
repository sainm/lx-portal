package com.lx.portal.appointment;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;

public record AppointmentRequest(
        @NotBlank(message = "请填写称呼") String nickname,
        @NotBlank(message = "请填写联系方式") String contact,
        String city,
        String ageRange,
        String consultationTarget,
        String concernDirection,
        String consultationMethod,
        String preferredTime,
        Long preferredCounselorId,
        boolean acceptsRecommendation,
        String problemSummary,
        @AssertTrue(message = "请同意隐私政策") boolean privacyAgreed,
        @AssertTrue(message = "请确认心理咨询不等同于医疗诊断或急救服务") boolean emergencyAcknowledged
) {
}

