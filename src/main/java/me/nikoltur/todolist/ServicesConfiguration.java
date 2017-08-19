package me.nikoltur.todolist;

import me.nikoltur.todolist.projects.ProjectsService;
import me.nikoltur.todolist.projects.ProjectsServiceImpl;
import me.nikoltur.todolist.tasks.TasksService;
import me.nikoltur.todolist.tasks.TasksServiceImpl;
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
    public ProjectsService projectsService() {
        return new ProjectsServiceImpl();
    }

    @Bean
    public TasksService tasksService() {
        return new TasksServiceImpl();
    }
}
