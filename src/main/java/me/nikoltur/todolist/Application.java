package me.nikoltur.todolist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * Main class used to start the Spring-application.
 *
 * @author Nikolas Turunen
 */
@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
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
