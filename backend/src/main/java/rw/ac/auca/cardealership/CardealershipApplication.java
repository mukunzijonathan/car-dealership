package rw.ac.auca.cardealership;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableRabbit
@ComponentScan(basePackages = "rw.ac.*")
@EnableJpaRepositories(basePackages = "rw.ac.auca")
@EnableMongoRepositories(basePackages = "rw.ac.auca")
@EntityScan(basePackages = "rw.ac.auca")
public class CardealershipApplication {

    public static void main(String[] args) {
        SpringApplication.run(CardealershipApplication.class, args);
    }
}
