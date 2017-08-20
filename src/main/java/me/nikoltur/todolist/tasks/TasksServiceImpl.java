package me.nikoltur.todolist.tasks;

import java.util.List;
import javax.transaction.Transactional;
import me.nikoltur.todolist.projects.ProjectDoesNotExistException;
import me.nikoltur.todolist.projects.da.ProjectsDao;
import me.nikoltur.todolist.tasks.da.Task;
import me.nikoltur.todolist.tasks.da.TasksDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Database implementation.
 *
 * @author Nikolas Turunen
 */
@Service
public class TasksServiceImpl implements TasksService {

    @Autowired
    private TasksDao tasksDao;
    @Autowired
    private ProjectsDao projectsDao;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public List<Task> getTasks(int projectId) {
        validateProjectId(projectId);

        return tasksDao.getAllOf(projectId);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void createTask(int projectId, String taskString) {
        validateProjectId(projectId);

        if (taskString == null) {
            throw new NullPointerException("Task string must not be null");
        }

        if (!projectsDao.exists(projectId)) {
            throw new ProjectDoesNotExistException("No project with the id " + projectId + " exists");
        }

        Task task = new Task();
        task.setProjectId(projectId);
        task.setTaskString(taskString);

        tasksDao.save(task);
    }

    /**
     * Validates the specified projectId.
     *
     * @param projectId Project id to be validated.
     * @throws IllegalArgumentException Thrown if the specified projectId is not valid.
     */
    private void validateProjectId(int projectId) {
        if (projectId <= 0) {
            throw new IllegalArgumentException("Project id must be greater than zero");
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void removeTask(int taskId) {
        validateTaskId(taskId);

        Task task = tasksDao.getById(taskId);

        if (task == null) {
            throw new TaskDoesNotExistException("No task with id " + taskId + " exists");
        }

        tasksDao.remove(task);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void editTask(int taskId, String newTask) {
        validateTaskId(taskId);

        if (newTask == null) {
            throw new NullPointerException("New task must not be null");
        }

        Task task = tasksDao.getById(taskId);
        if (task == null) {
            throw new TaskDoesNotExistException("No task with id " + taskId + " exists");
        }

        task.setTaskString(newTask);

        tasksDao.save(task);
    }

    /**
     * Validates the specified taskId.
     *
     * @param taskId Task id to be validated.
     * @throws IllegalArgumentException Thrown if the specified taskId is not valid.
     */
    private void validateTaskId(int taskId) {
        if (taskId <= 0) {
            throw new IllegalArgumentException("Task id must be greater than zero");
        }
    }
}
