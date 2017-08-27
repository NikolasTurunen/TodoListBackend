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
     * Returns a list containing all projects ordered by their position.
     *
     * @return A list containing all projects ordered by their position.
     */
    public List<Project> getProjects();

    /**
     * Creates a new project with the specified name.
     *
     * @param name Name of the project to be created.
     * @throws ProjectAlreadyExistsException Thrown if a project with the specified name already exists.
     * @throws NullPointerException Thrown if the specified name is null.
     * @throws IllegalArgumentException Thrown if the specified name is invalid.
     */
    public void createProject(String name);

    /**
     * Removes the project with the specified id.
     *
     * @param projectId Id of the project to be removed.
     * @throws ProjectDoesNotExistException Thrown if no project with the specified id exists.
     * @throws ProjectHasTasksException Thrown if the project with the specified id still has tasks referencing to it.
     * @throws IllegalArgumentException Thrown if the specified id is invalid.
     */
    public void removeProject(int projectId);

    /**
     * Renames the specified project with the specified name to the specified newName.
     *
     * @param name Name of the project to be renamed.
     * @param newName New name for the project.
     * @throws ProjectDoesNotExistException Thrown if no project with the specified name exists.
     * @throws ProjectAlreadyExistsException Thrown if a project with the specified newName already exists.
     * @throws NullPointerException Thrown if the specified name or newName is null.
     * @throws IllegalArgumentException Thrown if the specified name or newName is invalid.
     */
    public void renameProject(String name, String newName);

    /**
     * Swaps the positions of the specified projects.
     *
     * @param name Name of the first project.
     * @param name2 Name of the second project.
     * @throws ProjectDoesNotExistException Thrown if no project with the specified name or name2 exists.
     * @throws NullPointerException Thrown if the specified name or name2 is null.
     * @throws IllegalArgumentException Thrown if the specified name or name2 is invalid. Or if the specified name equals the specified name2.
     */
    public void swapPositionsOfProjects(String name, String name2);
}
