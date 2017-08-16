package me.nikoltur.todolist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main class used to start the Spring-application.
 *
 * @author Nikolas Turunen
 */
@SpringBootApplication
@EnableTransactionManagement
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
