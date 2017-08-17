package me.nikoltur.todolist.projects;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Nikolas Turunen
 */
@Configuration
public class TestConfiguration {

    @Bean
    public ProjectsResource projectsResource() {
        return new ProjectsResource();
    }
}
