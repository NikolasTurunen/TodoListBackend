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
     * Returns the project with the specified name. Null if no project with the specified name exists.
     *
     * @param name Name of the project to get.
     * @return The project with the specified name if it exists. Null otherwise.
     */
    public Project getByName(String name);

    /**
     * Saves the specified project.
     *
     * @param project Project to be saved.
     */
    public void save(Project project);

    /**
     * Removes the specified project.
     *
     * @param project Project to be removed.
     */
    public void remove(Project project);
}
