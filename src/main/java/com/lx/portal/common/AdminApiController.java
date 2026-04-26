package com.lx.portal.common;

import java.util.Map;
import java.util.Arrays;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.lx.portal.appointment.AppointmentService;
import com.lx.portal.appointment.Appointment;
import com.lx.portal.appointment.AppointmentDetailDto;
import com.lx.portal.appointment.AppointmentListDto;
import com.lx.portal.appointment.AppointmentRepository;
import com.lx.portal.appointment.AppointmentStatus;
import com.lx.portal.article.ArticleRepository;
import com.lx.portal.article.ArticleCategoryRepository;
import com.lx.portal.counselor.CounselorRepository;
import com.lx.portal.serviceitem.ServiceItemRepository;
import com.lx.portal.siteconfig.SiteConfigService;
import com.lx.portal.upload.UploadService;

@RestController
@RequestMapping("/api/admin")
public class AdminApiController {
    private final AppointmentRepository appointmentRepository;
    private final AppointmentService appointmentService;
    private final CounselorRepository counselorRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final ArticleRepository articleRepository;
    private final ArticleCategoryRepository articleCategoryRepository;
    private final SiteConfigService siteConfigService;
    private final UploadService uploadService;
    private final OperationLogService operationLogService;
    private final AnalyticsEventRepository analyticsEventRepository;

    public AdminApiController(AppointmentRepository appointmentRepository, AppointmentService appointmentService,
            CounselorRepository counselorRepository, ServiceItemRepository serviceItemRepository,
            ArticleRepository articleRepository, ArticleCategoryRepository articleCategoryRepository,
            SiteConfigService siteConfigService, UploadService uploadService,
            OperationLogService operationLogService, AnalyticsEventRepository analyticsEventRepository) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentService = appointmentService;
        this.counselorRepository = counselorRepository;
        this.serviceItemRepository = serviceItemRepository;
        this.articleRepository = articleRepository;
        this.articleCategoryRepository = articleCategoryRepository;
        this.siteConfigService = siteConfigService;
        this.uploadService = uploadService;
        this.operationLogService = operationLogService;
        this.analyticsEventRepository = analyticsEventRepository;
    }

    @GetMapping("/appointments")
    public ApiResponse<?> appointments(@RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) String concernDirection,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime startAt,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) java.time.LocalDateTime endAt,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int pageSize) {
        var pageable = PageRequest.of(Math.max(page - 1, 0), Math.min(pageSize, 100));
        String direction = concernDirection == null || concernDirection.isBlank() ? null : concernDirection;
        Specification<Appointment> spec = (root, query, cb) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (direction != null) {
                predicates.add(cb.equal(root.get("concernDirection"), direction));
            }
            if (startAt != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), startAt));
            }
            if (endAt != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), endAt));
            }
            query.orderBy(cb.desc(root.get("createdAt")));
            return cb.and(predicates.toArray(jakarta.persistence.criteria.Predicate[]::new));
        };
        return ApiResponse.ok(PageResponse.from(appointmentRepository.findAll(spec, pageable).map(AppointmentListDto::from)));
    }

    @GetMapping("/appointments/stats")
    public ApiResponse<?> appointmentStats() {
        return ApiResponse.ok(Arrays.stream(AppointmentStatus.values())
                .collect(Collectors.toMap(Enum::name, appointmentRepository::countByStatus)));
    }

    @GetMapping("/analytics/summary")
    public ApiResponse<?> analyticsSummary() {
        long appointmentFormOpen = analyticsEventRepository.countByEventName("appointment_form_open");
        long appointmentSubmit = appointmentRepository.count();
        long homeView = analyticsEventRepository.countByEventName("home_view");
        return ApiResponse.ok(Map.of(
                "homeView", homeView,
                "appointmentFormOpen", appointmentFormOpen,
                "appointmentSubmit", appointmentSubmit,
                "submitPerFormOpen", appointmentFormOpen == 0 ? 0 : (double) appointmentSubmit / appointmentFormOpen,
                "submitPerHomeView", homeView == 0 ? 0 : (double) appointmentSubmit / homeView));
    }

    @GetMapping(value = "/appointments/export", produces = "text/csv;charset=UTF-8")
    public void exportAppointments(HttpServletResponse response, Authentication authentication) throws java.io.IOException {
        response.setHeader("Content-Disposition", "attachment; filename=appointments.csv");
        response.getWriter().println("id,nickname,contact,city,ageRange,consultationTarget,concernDirection,consultationMethod,preferredTime,status,createdAt");
        for (var item : appointmentRepository.findAll()) {
            response.getWriter().printf("%d,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                    item.getId(),
                    csv(item.getNickname()),
                    csv(item.getContact()),
                    csv(item.getCity()),
                    csv(item.getAgeRange()),
                    csv(item.getConsultationTarget()),
                    csv(item.getConcernDirection()),
                    csv(item.getConsultationMethod()),
                    csv(item.getPreferredTime()),
                    item.getStatus(),
                    item.getCreatedAt());
        }
        operationLogService.record(authentication, "EXPORT_APPOINTMENTS", "appointment", null, "csv export");
    }

    @GetMapping("/me")
    public ApiResponse<?> me(Authentication authentication) {
        return ApiResponse.ok(Map.of("username", authentication.getName()));
    }

    @GetMapping("/appointments/{id}")
    public ApiResponse<?> appointment(@PathVariable Long id) {
        return ApiResponse.ok(appointmentRepository.findById(id).map(AppointmentDetailDto::from)
                .orElseThrow(() -> new IllegalArgumentException("预约不存在")));
    }

    @PatchMapping("/appointments/{id}/status")
    public ApiResponse<?> updateAppointmentStatus(@PathVariable Long id, @RequestBody Map<String, String> body,
            Authentication authentication) {
        var updated = appointmentService.updateStatus(id, AppointmentStatus.valueOf(body.get("status")));
        operationLogService.record(authentication, "UPDATE_APPOINTMENT_STATUS", "appointment", id, body.get("status"));
        return ApiResponse.ok(updated);
    }

    @PatchMapping("/appointments/{id}/note")
    public ApiResponse<?> updateAppointmentNote(@PathVariable Long id, @RequestBody Map<String, String> body,
            Authentication authentication) {
        var updated = appointmentService.updateNote(id, body.get("note"));
        operationLogService.record(authentication, "UPDATE_APPOINTMENT_NOTE", "appointment", id, "note updated");
        return ApiResponse.ok(updated);
    }

    @GetMapping("/counselors")
    public ApiResponse<?> counselors() {
        return ApiResponse.ok(counselorRepository.findAll());
    }

    @PostMapping("/counselors")
    public ApiResponse<?> createCounselor(@RequestBody com.lx.portal.counselor.Counselor counselor,
            Authentication authentication) {
        var saved = counselorRepository.save(counselor);
        operationLogService.record(authentication, "CREATE_COUNSELOR", "counselor", saved.getId(), saved.getName());
        return ApiResponse.ok(saved);
    }

    @PutMapping("/counselors/{id}")
    public ApiResponse<?> updateCounselor(@PathVariable Long id, @RequestBody com.lx.portal.counselor.Counselor input,
            Authentication authentication) {
        var counselor = counselorRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("咨询师不存在"));
        input.setStatus(input.getStatus());
        copyCounselor(input, counselor);
        var saved = counselorRepository.save(counselor);
        operationLogService.record(authentication, "UPDATE_COUNSELOR", "counselor", id, saved.getName());
        return ApiResponse.ok(saved);
    }

    @GetMapping("/services")
    public ApiResponse<?> services() {
        return ApiResponse.ok(serviceItemRepository.findAll());
    }

    @PostMapping("/services")
    public ApiResponse<?> createService(@RequestBody com.lx.portal.serviceitem.ServiceItem serviceItem,
            Authentication authentication) {
        var saved = serviceItemRepository.save(serviceItem);
        operationLogService.record(authentication, "CREATE_SERVICE", "service_item", saved.getId(), saved.getName());
        return ApiResponse.ok(saved);
    }

    @PutMapping("/services/{id}")
    public ApiResponse<?> updateService(@PathVariable Long id, @RequestBody com.lx.portal.serviceitem.ServiceItem input,
            Authentication authentication) {
        var service = serviceItemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("服务项目不存在"));
        copyService(input, service);
        var saved = serviceItemRepository.save(service);
        operationLogService.record(authentication, "UPDATE_SERVICE", "service_item", id, saved.getName());
        return ApiResponse.ok(saved);
    }

    @GetMapping("/articles")
    public ApiResponse<?> articles() {
        return ApiResponse.ok(articleRepository.findAll());
    }

    @GetMapping("/article-categories")
    public ApiResponse<?> articleCategories() {
        return ApiResponse.ok(articleCategoryRepository.findAll());
    }

    @PostMapping("/article-categories")
    public ApiResponse<?> createArticleCategory(@RequestBody com.lx.portal.article.ArticleCategory category,
            Authentication authentication) {
        var saved = articleCategoryRepository.save(category);
        operationLogService.record(authentication, "CREATE_ARTICLE_CATEGORY", "article_category", saved.getId(), saved.getName());
        return ApiResponse.ok(saved);
    }

    @PutMapping("/article-categories/{id}")
    public ApiResponse<?> updateArticleCategory(@PathVariable Long id,
            @RequestBody com.lx.portal.article.ArticleCategory input, Authentication authentication) {
        var category = articleCategoryRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("文章分类不存在"));
        category.setName(input.getName());
        category.setSlug(input.getSlug());
        category.setSortOrder(input.getSortOrder());
        category.setStatus(input.getStatus());
        var saved = articleCategoryRepository.save(category);
        operationLogService.record(authentication, "UPDATE_ARTICLE_CATEGORY", "article_category", id, saved.getName());
        return ApiResponse.ok(saved);
    }

    @PostMapping("/articles")
    public ApiResponse<?> createArticle(@RequestBody com.lx.portal.article.Article article,
            Authentication authentication) {
        var saved = articleRepository.save(article);
        operationLogService.record(authentication, "CREATE_ARTICLE", "article", saved.getId(), saved.getTitle());
        return ApiResponse.ok(saved);
    }

    @PutMapping("/articles/{id}")
    public ApiResponse<?> updateArticle(@PathVariable Long id, @RequestBody com.lx.portal.article.Article input,
            Authentication authentication) {
        var article = articleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("文章不存在"));
        copyArticle(input, article);
        var saved = articleRepository.save(article);
        operationLogService.record(authentication, "UPDATE_ARTICLE", "article", id, saved.getTitle());
        return ApiResponse.ok(saved);
    }

    @GetMapping("/site-config")
    public ApiResponse<?> siteConfig() {
        return ApiResponse.ok(siteConfigService.all());
    }

    @PatchMapping("/site-config/{key}")
    public ApiResponse<?> updateSiteConfig(@PathVariable String key, @RequestBody Map<String, String> body,
            Authentication authentication) {
        var saved = siteConfigService.put(key, body.getOrDefault("value", ""));
        operationLogService.record(authentication, "UPDATE_SITE_CONFIG", "site_config", saved.getId(), key);
        return ApiResponse.ok(saved);
    }

    @PostMapping("/uploads")
    public ApiResponse<?> upload(MultipartFile file) throws java.io.IOException {
        return ApiResponse.ok(uploadService.upload(file));
    }

    private static void copyCounselor(com.lx.portal.counselor.Counselor source, com.lx.portal.counselor.Counselor target) {
        target.setName(source.getName());
        target.setAvatarUrl(source.getAvatarUrl());
        target.setSummary(source.getSummary());
        target.setSpecialties(source.getSpecialties());
        target.setServiceAudience(source.getServiceAudience());
        target.setCounselingMethods(source.getCounselingMethods());
        target.setCounselingStyle(source.getCounselingStyle());
        target.setPriceAndDuration(source.getPriceAndDuration());
        target.setQualifications(source.getQualifications());
        target.setSortOrder(source.getSortOrder());
        target.setStatus(source.getStatus());
    }

    private static void copyService(com.lx.portal.serviceitem.ServiceItem source, com.lx.portal.serviceitem.ServiceItem target) {
        target.setName(source.getName());
        target.setSummary(source.getSummary());
        target.setSuitableFor(source.getSuitableFor());
        target.setCommonTopics(source.getCommonTopics());
        target.setCounselingMethods(source.getCounselingMethods());
        target.setSeoTitle(source.getSeoTitle());
        target.setSeoDescription(source.getSeoDescription());
        target.setSeoKeywords(source.getSeoKeywords());
        target.setSortOrder(source.getSortOrder());
        target.setStatus(source.getStatus());
    }

    private static void copyArticle(com.lx.portal.article.Article source, com.lx.portal.article.Article target) {
        target.setTitle(source.getTitle());
        target.setSlug(source.getSlug());
        target.setCoverUrl(source.getCoverUrl());
        target.setSummary(source.getSummary());
        target.setContent(source.getContent());
        target.setSeoTitle(source.getSeoTitle());
        target.setSeoDescription(source.getSeoDescription());
        target.setSeoKeywords(source.getSeoKeywords());
        target.setStatus(source.getStatus());
        target.setPublishedAt(source.getPublishedAt());
    }

    private static String csv(String value) {
        if (value == null) {
            return "";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}
