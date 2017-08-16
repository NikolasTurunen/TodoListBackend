package me.nikoltur.todolist.projects;

import java.util.List;
import me.nikoltur.todolist.projects.da.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest-resource to access and manage projects.
 *
 * @author Nikolas Turunen
 */
@RestController
public class ProjectsResource {

    private static final String BASE_PATH = "/projects";
    @Autowired
    private ProjectsService projectsService;

    /**
     * Returns a list of all projects.
     *
     * @return A list of all projects.
     */
    @GetMapping(BASE_PATH)
    public List<Project> getProjects() {
        return projectsService.getProjects();
    }

    /**
     * Adds a new project with the specified name.
     *
     * @param name Name of the project to be added.
     */
    @PostMapping(BASE_PATH + "/add")
    public void addProject(@RequestParam("name") String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Removes the project with the specified name.
     *
     * @param name Name of the project to be removed.
     */
    @PostMapping(BASE_PATH + "/remove")
    public void removeProject(@RequestParam("name") String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Changes the name of the project with the specified name to the specified newName.
     *
     * @param name Name of the project.
     * @param newName New name for the project.
     */
    @PostMapping(BASE_PATH + "/rename")
    public void renameProject(@RequestParam("name") String name, @RequestParam("newName") String newName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
