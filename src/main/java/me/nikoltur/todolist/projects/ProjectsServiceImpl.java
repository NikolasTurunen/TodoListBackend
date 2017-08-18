package me.nikoltur.todolist.projects;

import java.util.List;
import javax.transaction.Transactional;
import me.nikoltur.todolist.projects.da.Project;
import me.nikoltur.todolist.projects.da.ProjectsDao;
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

    @Override
    @Transactional(rollbackOn = Exception.class)
    public List<Project> getProjects() {
        return projectsDao.getAll();
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void createProject(String name) {
        if (name == null) {
            throw new NullPointerException("The specified name must not be null");
        }

        if (name.isEmpty()) {
            throw new IllegalArgumentException("The specified name must not be empty");
        }

        Project project = new Project();
        project.setName(name);
        projectsDao.save(project);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void removeProject(String name) {
        if (name == null) {
            throw new NullPointerException("The specified name must not be null");
        }

        if (name.isEmpty()) {
            throw new IllegalArgumentException("The specified name must not be empty");
        }

        Project project = projectsDao.getByName(name);
        if (project == null) {
            throw new ProjectDoesNotExistException("Project with the name " + name + " does not exist");
        }

        projectsDao.remove(project);
    }
}
