package rw.ac.auca.notification.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "audit_logs")
public class AuditLogDoc {

    @Id
    private String id;
    private String action;
    private String actorEmail;
    private String details;
    private String relatedSaleId;
    private LocalDateTime createdAt;
}
