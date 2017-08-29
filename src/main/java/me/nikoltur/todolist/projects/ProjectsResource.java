package me.nikoltur.todolist.projects;

import java.util.List;
import me.nikoltur.todolist.RestControllerConfiguration;
import me.nikoltur.todolist.projects.da.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest-resource to access and manage projects.
 *
 * @author Nikolas Turunen
 */
@RestController
@RequestMapping(RestControllerConfiguration.CONTEXT_PATH)
@CrossOrigin
public class ProjectsResource {

    private static final String BASE_PATH = "/projects";
    @Autowired
    private ProjectsService projectsService;

    /**
     * Returns a list of all projects ordered by their position.
     *
     * @return A list of all projects ordered by their position.
     */
    @GetMapping(BASE_PATH)
    public List<Project> getProjects() {
        return projectsService.getProjects();
    }

    /**
     * Creates a new project with the specified name.
     *
     * @param name Name of the project to be added.
     */
    @PostMapping(BASE_PATH + "/create")
    public void createProject(@RequestParam("name") String name) {
        projectsService.createProject(name);
    }

    /**
     * Removes the project with the specified id.
     *
     * @param projectId Id of the project to be removed.
     */
    @PostMapping(BASE_PATH + "/remove")
    public void removeProject(@RequestParam("projectId") int projectId) {
        projectsService.removeProject(projectId);
    }

    /**
     * Changes the name of the project with the specified id to the specified newName.
     *
     * @param projectId Id of the project to be renamed.
     * @param newName New name for the project.
     */
    @PostMapping(BASE_PATH + "/rename")
    public void renameProject(@RequestParam("projectId") int projectId, @RequestParam("newName") String newName) {
        projectsService.renameProject(projectId, newName);
    }

    /**
     * Swaps the positions of the specified projects.
     *
     * @param projectId Id of the first project.
     * @param projectId2 Id of the second project.
     */
    @PostMapping(BASE_PATH + "/swappositions")
    public void swapPositionsOfProjects(@RequestParam("projectId") int projectId, @RequestParam("projectId2") int projectId2) {
        projectsService.swapPositionsOfProjects(projectId, projectId2);
    }
}
