package rw.ac.auca.notification.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rw.ac.auca.notification.document.NotificationDoc;

@Repository
public interface NotificationRepository extends MongoRepository<NotificationDoc, String> {
}
