package me.nikoltur.todolist;

import me.nikoltur.todolist.projects.ProjectsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for services.
 *
 * @author Nikolas Turunen
 */
@Configuration
public class ServicesConfiguration {

    @Bean
    public ProjectsServiceImpl projectsService() {
        return new ProjectsServiceImpl();
    }
}
