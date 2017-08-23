package me.nikoltur.todolist.projects.da;

import java.util.List;

/**
 * Data-access object for the Project-entity.
 *
 * @author Nikolas Turunen
 */
public interface ProjectsDao {

    /**
     * Returns a list containing all projects ordered by their position.
     *
     * @return A list containing all projects ordered by their position.
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
     * Checks if a project with the specified id exists.
     *
     * @param projectId Id of the project to check.
     * @return True if a project with the specified id exists. False otherwise.
     */
    public boolean exists(int projectId);

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
