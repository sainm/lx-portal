package com.lx.portal.serviceitem;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lx.portal.common.PublishStatus;

public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long> {
    List<ServiceItem> findByStatusOrderBySortOrderAscIdAsc(PublishStatus status);
}

