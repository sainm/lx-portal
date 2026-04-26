package com.lx.portal.common;

import jakarta.validation.constraints.NotBlank;

public record TrackEventRequest(
        @NotBlank(message = "事件名称不能为空") String eventName,
        String pagePath,
        String target
) {
}
