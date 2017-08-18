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

    /**
     * Creates a new project with the specified name.
     *
     * @param name Name of the project to be created.
     * @throws ProjectAlreadyExistsException Thrown if a project with the specified name already exists.
     * @throws NullPointerException Thrown if the specified name is null.
     * @throws IllegalArgumentException Thrown if the specified name is empty.
     */
    public void createProject(String name);

    /**
     * Removes the project with the specified name.
     *
     * @param name Name of the project to be removed.
     * @throws ProjectDoesNotExistException Thrown if no project with the specified name exists.
     * @throws NullPointerException Thrown if the specified name is null.
     * @throws IllegalArgumentException Thrown if the specified name is empty.
     */
    public void removeProject(String name);
}
