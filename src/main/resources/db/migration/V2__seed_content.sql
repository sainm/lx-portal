insert into counselor (name, avatar_url, summary, specialties, service_audience, counseling_methods, counseling_style, price_and_duration, qualifications, sort_order, status, created_at, updated_at)
values
('林若晴', '', '擅长情绪压力、亲密关系与自我成长。', '情绪压力,亲密关系,自我成长', '成年人', '线上,线下', '温和、稳定，重视来访者的节奏与安全感。', '如确认展示价格，可配置 50 分钟/次', '心理咨询相关受训经历与持续督导。', 1, 'PUBLISHED', current_timestamp, current_timestamp),
('周明远', '', '关注青少年支持、亲子沟通与职场压力。', '青少年,亲子家庭,职场压力', '青少年,家长,职场人群', '线上,线下', '结构清晰，注重目标澄清与现实支持。', '如确认展示价格，可配置 50 分钟/次', '心理咨询相关受训经历与持续督导。', 2, 'PUBLISHED', current_timestamp, current_timestamp);

insert into service_item (name, summary, suitable_for, common_topics, counseling_methods, seo_title, seo_description, seo_keywords, sort_order, status, created_at, updated_at)
values
('个体咨询', '面向情绪、压力、关系和自我成长议题的个人咨询。', '希望理解自己并获得专业支持的成年人。', '焦虑情绪,压力管理,睡眠困扰,自我成长', '线上,线下', '个体心理咨询', '提供个体心理咨询预约与专业支持。', '心理咨询,个体咨询,情绪压力', 1, 'PUBLISHED', current_timestamp, current_timestamp),
('伴侣/婚姻咨询', '支持伴侣沟通、关系修复和冲突理解。', '正在经历关系冲突或沟通困难的伴侣。', '沟通冲突,信任议题,关系修复', '线上,线下', '伴侣婚姻心理咨询', '提供伴侣与婚姻关系心理咨询预约。', '婚姻咨询,伴侣咨询,亲密关系', 2, 'PUBLISHED', current_timestamp, current_timestamp),
('亲子/家庭咨询', '帮助家庭成员理解彼此并改善互动方式。', '亲子沟通困难、家庭压力较高的家庭。', '亲子沟通,家庭冲突,青少年支持', '线上,线下', '亲子家庭心理咨询', '提供亲子和家庭心理咨询预约。', '亲子咨询,家庭咨询,青少年心理', 3, 'PUBLISHED', current_timestamp, current_timestamp);

insert into article_category (name, slug, sort_order, status, created_at, updated_at)
values
('咨询指南', 'guide', 1, 'PUBLISHED', current_timestamp, current_timestamp),
('情绪压力', 'stress', 2, 'PUBLISHED', current_timestamp, current_timestamp);

insert into article (category_id, title, slug, cover_url, summary, content, seo_title, seo_description, seo_keywords, status, published_at, created_at, updated_at)
values
((select id from article_category where slug = 'guide'), '第一次心理咨询前，可以准备什么', 'first-session-guide', '', '第一次咨询前的准备建议。', '第一次咨询不需要把所有问题都整理清楚。你可以简单记录最近最困扰的事情、希望被支持的方向，以及对咨询的担心。心理咨询不是医疗急救，也不能替代诊断。', '第一次心理咨询准备', '第一次心理咨询前可以了解的准备事项。', '心理咨询,咨询指南', 'PUBLISHED', current_timestamp, current_timestamp, current_timestamp);

insert into site_config (config_key, config_value, created_at, updated_at)
values
('company_name', '安心心理咨询中心', current_timestamp, current_timestamp),
('phone', '400-000-0000', current_timestamp, current_timestamp),
('address', '请在后台配置详细地址', current_timestamp, current_timestamp),
('business_hours', '周一至周日 09:00-21:00', current_timestamp, current_timestamp),
('hero_title', '专业、保密、低压力的心理咨询预约', current_timestamp, current_timestamp),
('hero_subtitle', '为情绪压力、亲密关系、亲子家庭与职场困扰提供专业支持。', current_timestamp, current_timestamp),
('privacy_policy', '我们只为预约跟进和服务安排收集必要信息，并限制后台可见范围。', current_timestamp, current_timestamp),
('consultation_notice', '心理咨询不等同于医疗诊断、治疗或急救服务。如处于紧急危机，请立即联系当地急救或危机热线。', current_timestamp, current_timestamp),
('cookie_notice', '我们可能使用网站统计工具了解访问情况，以改进服务体验。', current_timestamp, current_timestamp);
