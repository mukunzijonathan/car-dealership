package rw.ac.auca.notification.document;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "notifications")
public class NotificationDoc {

    @Id
    private String id;
    private String channel;
    private String recipient;
    private String subject;
    private String message;
    private String relatedSaleId;
    private String status;
    private LocalDateTime createdAt;
}
