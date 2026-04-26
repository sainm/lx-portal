package com.lx.portal.common;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class OperationLogService {
    private final OperationLogRepository repository;

    public OperationLogService(OperationLogRepository repository) {
        this.repository = repository;
    }

    public void record(Authentication authentication, String action, String targetType, Long targetId, String detail) {
        OperationLog log = new OperationLog();
        log.setActor(authentication == null ? "system" : authentication.getName());
        log.setAction(action);
        log.setTargetType(targetType);
        log.setTargetId(targetId);
        log.setDetail(detail);
        repository.save(log);
    }
}

