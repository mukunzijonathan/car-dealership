package rw.ac.auca.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class SchemaFix implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        relax("sold_on");
        relax("employee_id");
    }

    private void relax(String column) {
        try {
            jdbcTemplate.execute("ALTER TABLE sale ALTER COLUMN " + column + " DROP NOT NULL");
        } catch (Exception ignored) {
            // Table may not exist on first boot; Hibernate creates it afterwards.
        }
    }
}
