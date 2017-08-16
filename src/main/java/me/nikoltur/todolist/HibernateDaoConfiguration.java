package me.nikoltur.todolist;

import me.nikoltur.todolist.projects.da.ProjectsDao;
import me.nikoltur.todolist.projects.da.ProjectsDaoImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Hibernate data-access objects.
 *
 * @author Nikolas Turunen
 */
@Configuration
public class HibernateDaoConfiguration {

    @Bean
    public ProjectsDao projectsDao() {
        return new ProjectsDaoImpl();
    }
}
