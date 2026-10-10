package rw.ac.auca.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import rw.ac.auca.notification.document.AuditLogDoc;
import rw.ac.auca.notification.document.NotificationDoc;
import rw.ac.auca.notification.repository.AuditLogRepository;
import rw.ac.auca.notification.repository.NotificationRepository;

import java.util.List;

@RestController
@RequestMapping("/notification")
public class NotificationController {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @GetMapping("/search-all")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','SALES_AGENT')")
    public List<NotificationDoc> findAllNotifications() {
        return notificationRepository.findAll();
    }

    @GetMapping("/audit/search-all")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public List<AuditLogDoc> findAllAuditLogs() {
        return auditLogRepository.findAll();
    }
}
