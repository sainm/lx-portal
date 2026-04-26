package com.lx.portal.article;

import java.time.LocalDateTime;

public record ArticleDto(
        Long id,
        String title,
        String slug,
        String coverUrl,
        String summary,
        String content,
        String seoTitle,
        String seoDescription,
        String seoKeywords,
        LocalDateTime publishedAt
) {
    public static ArticleDto from(Article article) {
        return new ArticleDto(
                article.getId(),
                article.getTitle(),
                article.getSlug(),
                article.getCoverUrl(),
                article.getSummary(),
                article.getContent(),
                article.getSeoTitle(),
                article.getSeoDescription(),
                article.getSeoKeywords(),
                article.getPublishedAt());
    }
}

