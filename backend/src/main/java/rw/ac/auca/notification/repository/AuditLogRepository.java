package rw.ac.auca.notification.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import rw.ac.auca.notification.document.AuditLogDoc;

@Repository
public interface AuditLogRepository extends MongoRepository<AuditLogDoc, String> {
}
