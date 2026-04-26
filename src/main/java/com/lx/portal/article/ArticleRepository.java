package com.lx.portal.article;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.lx.portal.common.PublishStatus;

public interface ArticleRepository extends JpaRepository<Article, Long> {
    Page<Article> findByStatus(PublishStatus status, Pageable pageable);
    Optional<Article> findBySlugAndStatus(String slug, PublishStatus status);
}

