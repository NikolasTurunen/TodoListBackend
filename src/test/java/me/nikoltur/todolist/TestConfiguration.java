package me.nikoltur.todolist;


import me.nikoltur.todolist.DatabaseWiper;
import me.nikoltur.todolist.projects.ProjectsResource;
import me.nikoltur.todolist.tasks.TasksResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Nikolas Turunen
 */
@Configuration
public class TestConfiguration {

    @Bean
    public DatabaseWiper databaseWiper() {
        return new DatabaseWiper();
    }

    @Bean
    public ProjectsResource projectsResource() {
        return new ProjectsResource();
    }

    @Bean
    public TasksResource tasksResource() {
        return new TasksResource();
    }
}
