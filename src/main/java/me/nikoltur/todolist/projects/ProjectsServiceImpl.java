package me.nikoltur.todolist.projects;

import java.util.List;
import javax.transaction.Transactional;
import me.nikoltur.todolist.projects.da.Project;
import me.nikoltur.todolist.projects.da.ProjectsDao;
import me.nikoltur.todolist.tasks.da.TasksDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Database implementation.
 *
 * @author Nikolas Turunen
 */
@Service
public class ProjectsServiceImpl implements ProjectsService {

    @Autowired
    private ProjectsDao projectsDao;
    @Autowired
    private TasksDao tasksDao;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public List<Project> getProjects() {
        return projectsDao.getAll();
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void createProject(String name) {
        validateName(name);

        verifyProjectDoesNotExist(name);

        Project project = new Project();
        project.setName(name);
        projectsDao.save(project);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void removeProject(String name) {
        validateName(name);

        Project project = projectsDao.getByName(name);
        if (project == null) {
            throw new ProjectDoesNotExistException("Project with the name " + name + " does not exist");
        }

        if (!tasksDao.getAllOf(project.getId()).isEmpty()) {
            throw new ProjectHasTasksException("Project with the name " + name + " cannot be removed because there are tasks referencing to it");
        }

        projectsDao.remove(project);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void renameProject(String name, String newName) {
        validateName(name);
        validateName(newName);

        verifyProjectDoesNotExist(newName);

        Project project = projectsDao.getByName(name);
        if (project == null) {
            throw new ProjectDoesNotExistException("No project with the specified name exists");
        }

        project.setName(newName);

        projectsDao.save(project);
    }

    /**
     * Validates the specified project name.
     *
     * @param name Name to be validated.
     * @throws NullPointerException Thrown if the specified name is null.
     * @throws IllegalArgumentException Thrown if the specified name is empty or whitespace-only.
     */
    private void validateName(String name) {
        if (name == null) {
            throw new NullPointerException("Project name must not be null");
        }

        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("Project name must not be empty or whitespace-only");
        }
    }

    /**
     * Verifies that no project with the specified name exists.
     *
     * @param name Name of the project to check.
     * @throws ProjectAlreadyExistsException Thrown if a project with the specified name exists.
     */
    private void verifyProjectDoesNotExist(String name) {
        Project existingProject = projectsDao.getByName(name);
        if (existingProject != null) {
            throw new ProjectAlreadyExistsException("Project with the name " + name + " already exists");
        }
    }
}
