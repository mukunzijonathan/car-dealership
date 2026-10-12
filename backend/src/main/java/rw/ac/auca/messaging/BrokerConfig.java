package rw.ac.auca.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BrokerConfig {

    public static final String EXCHANGE = "cdms.events";
    public static final String SALE_QUEUE = "cdms.sale.created";
    public static final String EMAIL_QUEUE = "cdms.notification.email";
    public static final String SMS_QUEUE = "cdms.notification.sms";
    public static final String SALE_KEY = "sale.created";
    public static final String ACCOUNT_QUEUE = "cdms.account.registered";
    public static final String EMAIL_KEY = "notification.email";
    public static final String SMS_KEY = "notification.sms";
    public static final String ACCOUNT_KEY = "account.registered";

    @Bean
    public TopicExchange cdmsExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue saleQueue() {
        return new Queue(SALE_QUEUE, true);
    }

    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, true);
    }

    @Bean
    public Queue smsQueue() {
        return new Queue(SMS_QUEUE, true);
    }

    @Bean
    public Binding saleBinding(Queue saleQueue, TopicExchange cdmsExchange) {
        return BindingBuilder.bind(saleQueue).to(cdmsExchange).with(SALE_KEY);
    }

    @Bean
    public Binding emailBinding(Queue emailQueue, TopicExchange cdmsExchange) {
        return BindingBuilder.bind(emailQueue).to(cdmsExchange).with(EMAIL_KEY);
    }

    @Bean
    public Binding smsBinding(Queue smsQueue, TopicExchange cdmsExchange) {
        return BindingBuilder.bind(smsQueue).to(cdmsExchange).with(SMS_KEY);
    }

    @Bean
    public Queue accountQueue() {
        return new Queue(ACCOUNT_QUEUE, true);
    }

    @Bean
    public Binding accountBinding(Queue accountQueue, TopicExchange cdmsExchange) {
        return BindingBuilder.bind(accountQueue).to(cdmsExchange).with(ACCOUNT_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper typeMapper =
                new org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper();
        typeMapper.setTrustedPackages("rw.ac.auca.messaging");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(converter);
        return factory;
    }
}
