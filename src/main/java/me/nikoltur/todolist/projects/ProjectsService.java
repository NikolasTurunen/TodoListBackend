package me.nikoltur.todolist.projects;

import java.util.List;
import me.nikoltur.todolist.projects.da.Project;

/**
 * Service to access and control projects.
 *
 * @author Nikolas Turunen
 */
public interface ProjectsService {

    /**
     * Returns a list containing all projects.
     *
     * @return A list containing all projects.
     */
    public List<Project> getProjects();
}
