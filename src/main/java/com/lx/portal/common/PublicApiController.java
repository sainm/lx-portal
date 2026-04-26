package com.lx.portal.common;

import java.net.URI;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lx.portal.appointment.Appointment;
import com.lx.portal.appointment.AppointmentRequest;
import com.lx.portal.appointment.AppointmentService;
import com.lx.portal.article.ArticleRepository;
import com.lx.portal.article.ArticleDto;
import com.lx.portal.common.PublishStatus;
import com.lx.portal.counselor.CounselorDto;
import com.lx.portal.counselor.CounselorRepository;
import com.lx.portal.serviceitem.ServiceItemDto;
import com.lx.portal.serviceitem.ServiceItemRepository;
import com.lx.portal.siteconfig.SiteConfigService;

@RestController
@RequestMapping("/api/public")
public class PublicApiController {
    private final SiteConfigService siteConfigService;
    private final CounselorRepository counselorRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final ArticleRepository articleRepository;
    private final AppointmentService appointmentService;
    private final AnalyticsEventRepository analyticsEventRepository;

    public PublicApiController(SiteConfigService siteConfigService, CounselorRepository counselorRepository,
            ServiceItemRepository serviceItemRepository, ArticleRepository articleRepository,
            AppointmentService appointmentService, AnalyticsEventRepository analyticsEventRepository) {
        this.siteConfigService = siteConfigService;
        this.counselorRepository = counselorRepository;
        this.serviceItemRepository = serviceItemRepository;
        this.articleRepository = articleRepository;
        this.appointmentService = appointmentService;
        this.analyticsEventRepository = analyticsEventRepository;
    }

    @GetMapping("/site-config")
    public ApiResponse<Map<String, String>> siteConfig() {
        return ApiResponse.ok(siteConfigService.all());
    }

    @GetMapping("/counselors")
    public ApiResponse<?> counselors() {
        return ApiResponse.ok(counselorRepository.findByStatusOrderBySortOrderAscIdAsc(PublishStatus.PUBLISHED)
                .stream().map(CounselorDto::from).toList());
    }

    @GetMapping("/counselors/{id}")
    public ApiResponse<?> counselor(@PathVariable Long id) {
        return ApiResponse.ok(counselorRepository.findById(id).map(CounselorDto::from)
                .orElseThrow(() -> new IllegalArgumentException("咨询师不存在")));
    }

    @GetMapping("/services")
    public ApiResponse<?> services() {
        return ApiResponse.ok(serviceItemRepository.findByStatusOrderBySortOrderAscIdAsc(PublishStatus.PUBLISHED)
                .stream().map(ServiceItemDto::from).toList());
    }

    @GetMapping("/services/{id}")
    public ApiResponse<?> service(@PathVariable Long id) {
        return ApiResponse.ok(serviceItemRepository.findById(id).map(ServiceItemDto::from)
                .orElseThrow(() -> new IllegalArgumentException("服务项目不存在")));
    }

    @GetMapping("/articles")
    public ApiResponse<?> articles() {
        return ApiResponse.ok(PageResponse.from(articleRepository.findByStatus(PublishStatus.PUBLISHED, PageRequest.of(0, 20))
                .map(ArticleDto::from)));
    }

    @GetMapping("/articles/{slug}")
    public ApiResponse<?> article(@PathVariable String slug) {
        return ApiResponse.ok(articleRepository.findBySlugAndStatus(slug, PublishStatus.PUBLISHED).map(ArticleDto::from)
                .orElseThrow(() -> new IllegalArgumentException("文章不存在")));
    }

    @PostMapping("/appointments")
    public ApiResponse<Map<String, Object>> createAppointment(@Valid @RequestBody AppointmentRequest request) {
        Appointment appointment = appointmentService.create(request);
        return ApiResponse.ok(Map.of("id", appointment.getId(), "successUrl", URI.create("/appointment-success").toString()));
    }

    @PostMapping("/track")
    public ApiResponse<Void> track(@Valid @RequestBody TrackEventRequest request) {
        AnalyticsEvent event = new AnalyticsEvent();
        event.setEventName(request.eventName());
        event.setPagePath(request.pagePath());
        event.setTarget(request.target());
        analyticsEventRepository.save(event);
        return ApiResponse.ok();
    }
}
