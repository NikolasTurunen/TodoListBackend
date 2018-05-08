package me.nikoltur.todolist;

import me.nikoltur.springglobalconfiguration.GlobalHttpRequestLoggingConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main class used to start the Spring-application.
 *
 * @author Nikolas Turunen
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableAutoConfiguration(exclude = HibernateJpaAutoConfiguration.class)
@Import(GlobalHttpRequestLoggingConfiguration.class)
public class Application {

    /**
     * Starts the Spring-application.
     *
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
