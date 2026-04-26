package com.lx.portal.counselor;

public record CounselorDto(
        Long id,
        String name,
        String avatarUrl,
        String summary,
        String specialties,
        String serviceAudience,
        String counselingMethods,
        String counselingStyle,
        String priceAndDuration,
        String qualifications
) {
    public static CounselorDto from(Counselor counselor) {
        return new CounselorDto(
                counselor.getId(),
                counselor.getName(),
                counselor.getAvatarUrl(),
                counselor.getSummary(),
                counselor.getSpecialties(),
                counselor.getServiceAudience(),
                counselor.getCounselingMethods(),
                counselor.getCounselingStyle(),
                counselor.getPriceAndDuration(),
                counselor.getQualifications());
    }
}

