package rw.ac.auca.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SaleEventPublisher {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void publishSaleCreated(SaleCreatedEvent event) {
        rabbitTemplate.convertAndSend(BrokerConfig.EXCHANGE, BrokerConfig.SALE_KEY, event);
    }
}
