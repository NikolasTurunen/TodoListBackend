package me.nikoltur.todolist.projects.da;

import java.util.List;

/**
 * Data-access object for the Project-entity.
 *
 * @author Nikolas Turunen
 */
public interface ProjectsDao {

    /**
     * Returns a list containing all projects.
     *
     * @return A list containing all projects.
     */
    public List<Project> getAll();

    /**
     * Saves the specified project.
     *
     * @param project Project to be saved.
     */
    public void save(Project project);
}
