package com.lx.portal.serviceitem;

public record ServiceItemDto(
        Long id,
        String name,
        String summary,
        String suitableFor,
        String commonTopics,
        String counselingMethods,
        String seoTitle,
        String seoDescription,
        String seoKeywords
) {
    public static ServiceItemDto from(ServiceItem serviceItem) {
        return new ServiceItemDto(
                serviceItem.getId(),
                serviceItem.getName(),
                serviceItem.getSummary(),
                serviceItem.getSuitableFor(),
                serviceItem.getCommonTopics(),
                serviceItem.getCounselingMethods(),
                serviceItem.getSeoTitle(),
                serviceItem.getSeoDescription(),
                serviceItem.getSeoKeywords());
    }
}

