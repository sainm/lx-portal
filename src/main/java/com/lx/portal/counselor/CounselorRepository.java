package com.lx.portal.counselor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.lx.portal.common.PublishStatus;

public interface CounselorRepository extends JpaRepository<Counselor, Long> {
    List<Counselor> findByStatusOrderBySortOrderAscIdAsc(PublishStatus status);
    Page<Counselor> findByStatus(PublishStatus status, Pageable pageable);
}

