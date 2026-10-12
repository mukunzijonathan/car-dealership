package rw.ac.auca.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import rw.ac.auca.notification.document.AuditLogDoc;
import rw.ac.auca.notification.document.NotificationDoc;
import rw.ac.auca.notification.repository.AuditLogRepository;
import rw.ac.auca.notification.repository.NotificationRepository;

import java.time.LocalDateTime;

@Component
public class SaleEventListener {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @RabbitListener(queues = BrokerConfig.ACCOUNT_QUEUE)
    public void onAccountRegistered(AccountRegisteredEvent event) {
        saveNotice("EMAIL", event.customerEmail(),
                "Welcome to CDMS",
                "Hello " + event.fullName() + ". Your customer account is ready. You can request a car on the Kigali lot.",
                "welcome");
        saveNotice("SMS", event.customerPhone(),
                "Welcome SMS",
                "CDMS: account created for " + event.fullName() + ".",
                "welcome");
        System.out.println("[EMAIL] welcome to " + event.customerEmail());
        System.out.println("[SMS] welcome to " + event.customerPhone());
    }

    @RabbitListener(queues = BrokerConfig.SALE_QUEUE)
    public void onSaleCreated(SaleCreatedEvent event) {
        AuditLogDoc log = new AuditLogDoc();
        log.setAction("SALE_CREATED");
        log.setActorEmail(event.employeeEmail());
        log.setRelatedSaleId(String.valueOf(event.saleId()));
        log.setDetails("Car " + event.vin() + " sold for RWF " + event.amount());
        log.setCreatedAt(LocalDateTime.now());
        auditLogRepository.save(log);

        rabbitTemplate.convertAndSend(BrokerConfig.EXCHANGE, BrokerConfig.EMAIL_KEY, event);
        rabbitTemplate.convertAndSend(BrokerConfig.EXCHANGE, BrokerConfig.SMS_KEY, event);
    }

    @RabbitListener(queues = BrokerConfig.EMAIL_QUEUE)
    public void onEmail(SaleCreatedEvent event) {
        saveNotice("EMAIL", event.customerEmail(),
                "Sale confirmation",
                "Thank you. Your purchase of VIN " + event.vin() + " for RWF " + event.amount() + " is confirmed.",
                event.saleId() == null ? null : String.valueOf(event.saleId()));
        System.out.println("[EMAIL] to " + event.customerEmail() + " — sale " + event.saleId());
    }

    @RabbitListener(queues = BrokerConfig.SMS_QUEUE)
    public void onSms(SaleCreatedEvent event) {
        saveNotice("SMS", event.customerPhone(),
                "Sale SMS",
                "CDMS: sale confirmed for VIN " + event.vin() + ".",
                event.saleId() == null ? null : String.valueOf(event.saleId()));
        System.out.println("[SMS] to " + event.customerPhone() + " — sale " + event.saleId());
    }

    private void saveNotice(String channel, String recipient, String subject, String message, String relatedSaleId) {
        NotificationDoc doc = new NotificationDoc();
        doc.setChannel(channel);
        doc.setRecipient(recipient);
        doc.setSubject(subject);
        doc.setMessage(message);
        doc.setRelatedSaleId(relatedSaleId);
        doc.setStatus("SENT");
        doc.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(doc);
    }
}
