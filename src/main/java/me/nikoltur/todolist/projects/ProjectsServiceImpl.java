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
 * Thread safe.
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
    public synchronized void createProject(String name) {
        validateName(name);

        verifyProjectDoesNotExist(name);

        int position = projectsDao.getAll().size();

        Project project = new Project();
        project.setName(name);
        project.setPosition(position);

        projectsDao.save(project);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public synchronized void removeProject(int projectId) {
        validateId(projectId);

        Project project = projectsDao.getById(projectId);
        if (project == null) {
            throw new ProjectDoesNotExistException("Project with the id " + projectId + " does not exist");
        }

        if (!tasksDao.getAllOf(project.getId()).isEmpty()) {
            throw new ProjectHasTasksException("Project with the id " + projectId + " cannot be removed because there are tasks referencing to it");
        }

        projectsDao.remove(project);

        decrementPositionsOfProjectsWithHigherPosition(project.getPosition());
    }

    /**
     * Decrements positions of all projects that have a higher position than the specified position.
     *
     * @param position Position threshold.
     */
    private void decrementPositionsOfProjectsWithHigherPosition(int position) {
        List<Project> projects = projectsDao.getAll();
        for (Project project : projects) {
            if (project.getPosition() > position) {
                project.setPosition(project.getPosition() - 1);
                projectsDao.save(project);
            }
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public synchronized void renameProject(int projectId, String newName) {
        validateId(projectId);
        validateName(newName);

        Project project = projectsDao.getById(projectId);
        if (project == null) {
            throw new ProjectDoesNotExistException("No project with the id " + projectId + " exists");
        }

        if (project.getName().equals(newName)) {
            return;
        }

        verifyProjectDoesNotExist(newName);

        project.setName(newName);

        projectsDao.save(project);
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

    @Override
    @Transactional(rollbackOn = Exception.class)
    public synchronized void swapPositionsOfProjects(int projectId, int projectId2) {
        validateId(projectId);
        validateId(projectId2);

        if (projectId == projectId2) {
            throw new IllegalArgumentException("Project cannot swap position with itself");
        }

        Project project1 = projectsDao.getById(projectId);
        if (project1 == null) {
            throw new ProjectDoesNotExistException("Project with the name " + projectId + " does not exist");
        }

        Project project2 = projectsDao.getById(projectId2);
        if (project2 == null) {
            throw new ProjectDoesNotExistException("Project with the name " + projectId2 + " does not exist");
        }

        boolean project2IsPreviousFromProject1 = project2.getPosition() == project1.getPosition() + 1;
        boolean project2IsNextFromProject = project2.getPosition() == project1.getPosition() - 1;
        if (!project2IsPreviousFromProject1 && !project2IsNextFromProject) {
            throw new IllegalArgumentException("The specified second project must be either previous or next from the specified first project");
        }

        int positionOfProject1 = project1.getPosition();
        project1.setPosition(project2.getPosition());
        project2.setPosition(positionOfProject1);

        projectsDao.save(project1);
        projectsDao.save(project2);
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
     * Validates the specified project id.
     *
     * @param projectId Project id to be validated.
     * @throws IllegalArgumentException Thrown if the specified id is zero or negative.
     */
    private void validateId(int projectId) {
        if (projectId <= 0) {
            throw new IllegalArgumentException("Project id must not be zero or negative");
        }
    }
}
