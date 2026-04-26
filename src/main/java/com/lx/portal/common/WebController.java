package com.lx.portal.common;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.lx.portal.article.ArticleRepository;
import com.lx.portal.common.PublishStatus;
import com.lx.portal.counselor.CounselorRepository;
import com.lx.portal.serviceitem.ServiceItemRepository;
import com.lx.portal.siteconfig.SiteConfigService;
import java.util.List;
import java.util.Map;

@Controller
public class WebController {
    private final SiteConfigService siteConfigService;
    private final CounselorRepository counselorRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final ArticleRepository articleRepository;

    public WebController(SiteConfigService siteConfigService, CounselorRepository counselorRepository,
            ServiceItemRepository serviceItemRepository, ArticleRepository articleRepository) {
        this.siteConfigService = siteConfigService;
        this.counselorRepository = counselorRepository;
        this.serviceItemRepository = serviceItemRepository;
        this.articleRepository = articleRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("config", siteConfigService.all());
        model.addAttribute("counselors", counselorRepository.findAll());
        model.addAttribute("services", serviceItemRepository.findAll());
        model.addAttribute("articles", articleRepository.findAll());
        return "index";
    }

    @GetMapping("/appointment")
    public String appointment(Model model) {
        model.addAttribute("config", siteConfigService.all());
        model.addAttribute("counselors", counselorRepository.findAll());
        return "appointment";
    }

    @GetMapping("/appointment-success")
    public String appointmentSuccess(Model model) {
        model.addAttribute("config", siteConfigService.all());
        return "appointment-success";
    }

    @GetMapping("/services")
    public String services(Model model) {
        model.addAttribute("title", "服务项目");
        model.addAttribute("eyebrow", "咨询服务");
        model.addAttribute("description", "按议题选择适合的咨询方向。");
        model.addAttribute("items", serviceItemRepository.findAll().stream()
                .map(item -> Map.of("title", item.getName(), "body", item.getSummary() == null ? "" : item.getSummary()))
                .toList());
        return "simple-page";
    }

    @GetMapping("/services/{id}")
    public String serviceDetail(@PathVariable Long id, Model model) {
        var item = serviceItemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("服务项目不存在"));
        model.addAttribute("title", item.getName());
        model.addAttribute("eyebrow", "服务项目");
        model.addAttribute("description", item.getSummary());
        model.addAttribute("blocks", List.of(
                Map.of("title", "适合人群", "body", nullToBlank(item.getSuitableFor())),
                Map.of("title", "常见议题", "body", nullToBlank(item.getCommonTopics())),
                Map.of("title", "咨询方式", "body", nullToBlank(item.getCounselingMethods()))));
        return "detail-page";
    }

    @GetMapping("/counselors")
    public String counselors(Model model) {
        model.addAttribute("title", "咨询师团队");
        model.addAttribute("eyebrow", "专业支持");
        model.addAttribute("description", "了解咨询师的擅长方向、服务人群与咨询风格。");
        model.addAttribute("items", counselorRepository.findAll().stream()
                .map(item -> Map.of("title", item.getName(), "body", item.getSummary() == null ? "" : item.getSummary()))
                .toList());
        return "simple-page";
    }

    @GetMapping("/counselors/{id}")
    public String counselorDetail(@PathVariable Long id, Model model) {
        var item = counselorRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("咨询师不存在"));
        model.addAttribute("title", item.getName());
        model.addAttribute("eyebrow", "咨询师详情");
        model.addAttribute("description", item.getSummary());
        model.addAttribute("blocks", List.of(
                Map.of("title", "擅长方向", "body", nullToBlank(item.getSpecialties())),
                Map.of("title", "服务人群", "body", nullToBlank(item.getServiceAudience())),
                Map.of("title", "咨询风格", "body", nullToBlank(item.getCounselingStyle())),
                Map.of("title", "资质与受训经历", "body", nullToBlank(item.getQualifications()))));
        return "detail-page";
    }

    @GetMapping("/articles")
    public String articles(Model model) {
        model.addAttribute("articles", articleRepository.findByStatus(PublishStatus.PUBLISHED,
                org.springframework.data.domain.PageRequest.of(0, 20)).getContent());
        return "articles";
    }

    @GetMapping("/articles/{slug}")
    public String articleDetail(@PathVariable String slug, Model model) {
        var article = articleRepository.findBySlugAndStatus(slug, PublishStatus.PUBLISHED)
                .orElseThrow(() -> new IllegalArgumentException("文章不存在"));
        model.addAttribute("title", article.getTitle());
        model.addAttribute("eyebrow", "心理科普");
        model.addAttribute("description", article.getSummary());
        model.addAttribute("blocks", List.of(Map.of("title", "正文", "body", nullToBlank(article.getContent()))));
        return "detail-page";
    }

    @GetMapping("/about")
    public String about(Model model) {
        var config = siteConfigService.all();
        model.addAttribute("title", "关于我们");
        model.addAttribute("eyebrow", "安心心理");
        model.addAttribute("description", "我们提供专业、保密、边界清晰的心理咨询预约服务。");
        model.addAttribute("items", List.of(
                Map.of("title", "联系电话", "body", config.getOrDefault("phone", "")),
                Map.of("title", "地址", "body", config.getOrDefault("address", "")),
                Map.of("title", "营业时间", "body", config.getOrDefault("business_hours", ""))));
        return "simple-page";
    }

    @GetMapping("/process")
    public String process(Model model) {
        model.addAttribute("title", "咨询流程");
        model.addAttribute("eyebrow", "清晰开始");
        model.addAttribute("description", "从提交预约到开始咨询，每一步都清楚。");
        model.addAttribute("items", List.of(
                Map.of("title", "提交预约", "body", "填写基本情况、咨询方向与偏好时间。"),
                Map.of("title", "联系确认", "body", "工作人员联系确认需求、时间和咨询方式。"),
                Map.of("title", "匹配咨询师", "body", "根据议题、方式、时间匹配或确认指定咨询师。"),
                Map.of("title", "开始咨询", "body", "在约定时间进行线上或线下咨询。")));
        return "simple-page";
    }

    @GetMapping("/faq")
    public String faq(Model model) {
        model.addAttribute("title", "常见问题");
        model.addAttribute("eyebrow", "FAQ");
        model.addAttribute("description", "这里整理第一次咨询前最常见的疑问。");
        model.addAttribute("items", List.of(
                Map.of("title", "第一次咨询需要准备什么？", "body", "可以简单记录最近最困扰的事情，以及希望获得支持的方向。"),
                Map.of("title", "心理咨询能不能开药？", "body", "心理咨询不提供开药服务，也不替代医疗诊断。"),
                Map.of("title", "信息是否会保密？", "body", "预约信息仅用于联系确认和服务安排，并限制后台可见范围。")));
        return "simple-page";
    }

    @GetMapping("/privacy")
    public String privacy(Model model) {
        model.addAttribute("title", "隐私政策");
        model.addAttribute("eyebrow", "隐私保护");
        model.addAttribute("description", siteConfigService.all().getOrDefault("privacy_policy", "我们重视并保护你的预约信息。"));
        model.addAttribute("items", List.of(Map.of("title", "信息使用范围", "body", "预约信息仅用于联系确认、咨询匹配与服务安排。")));
        return "simple-page";
    }

    @GetMapping("/notice")
    public String notice(Model model) {
        model.addAttribute("title", "咨询须知");
        model.addAttribute("eyebrow", "服务边界");
        model.addAttribute("description", siteConfigService.all().getOrDefault("consultation_notice", "心理咨询不等同于医疗诊断或急救服务。"));
        model.addAttribute("items", List.of(Map.of("title", "紧急情况", "body", "如处于立即危险，请联系当地急救、警方或可信赖的身边人。")));
        return "simple-page";
    }

    private static String nullToBlank(String value) {
        return value == null ? "" : value;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping({"/admin", "/admin/dashboard"})
    public String dashboard() {
        return "admin/dashboard";
    }

    @GetMapping("/admin/appointments")
    public String adminAppointments() {
        return "admin/appointments";
    }

    @GetMapping("/admin/counselors")
    public String adminCounselors() {
        return "admin/counselors";
    }

    @GetMapping("/admin/services")
    public String adminServices() {
        return "admin/services";
    }

    @GetMapping("/admin/articles")
    public String adminArticles() {
        return "admin/articles";
    }

    @GetMapping("/admin/chats")
    public String adminChats() {
        return "admin/chats";
    }
}
