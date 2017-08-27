package me.nikoltur.todolist.tasks;

import java.util.List;
import java.util.Objects;
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
        validateTaskString(taskString);

        if (!projectsDao.exists(projectId)) {
            throw new ProjectDoesNotExistException("No project with the id " + projectId + " exists");
        }

        int position = getTasks(projectId).size();

        Task task = new Task();
        task.setProjectId(projectId);
        task.setTaskString(taskString);
        task.setPosition(position);

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

        List<Task> remainingTasks;
        if (task.getProjectId() != null) {
            remainingTasks = tasksDao.getAllOf(task.getProjectId());
        } else {
            remainingTasks = tasksDao.getById(task.getParentTaskId()).getDetails();
        }
        for (Task remainingTask : remainingTasks) {
            if (remainingTask.getPosition() > task.getPosition()) {
                remainingTask.setPosition(remainingTask.getPosition() - 1);
                tasksDao.save(remainingTask);
            }
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void editTask(int taskId, String newTask) {
        validateTaskId(taskId);
        validateTaskString(newTask);

        Task task = tasksDao.getById(taskId);
        if (task == null) {
            throw new TaskDoesNotExistException("No task with id " + taskId + " exists");
        }

        task.setTaskString(newTask);

        tasksDao.save(task);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void createDetail(int taskId, String detail) {
        validateTaskId(taskId);
        validateTaskString(detail);

        Task parentTask = tasksDao.getById(taskId);
        if (parentTask == null) {
            throw new TaskDoesNotExistException("No task with id " + taskId + " exists");
        }

        int position = parentTask.getDetails().size();

        Task task = new Task();
        task.setParentTaskId(taskId);
        task.setTaskString(detail);
        task.setPosition(position);

        tasksDao.save(task);
    }

    /**
     * Validates the specified taskString.
     *
     * @param taskString Task string to be validated.
     * @throws NullPointerException Thrown if the specified taskString is null.
     */
    private void validateTaskString(String taskString) {
        if (taskString == null) {
            throw new NullPointerException("Task string cannot be null");
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void swapPositionsOfTasks(int taskId, int taskId2) {
        validateTaskId(taskId);
        validateTaskId(taskId2);

        if (taskId == taskId2) {
            throw new IllegalArgumentException("Cannot swap position with itself");
        }

        Task task1 = tasksDao.getById(taskId);
        if (task1 == null) {
            throw new TaskDoesNotExistException("No task with id " + taskId + " exists");
        }

        Task task2 = tasksDao.getById(taskId2);
        if (task2 == null) {
            throw new TaskDoesNotExistException("No task with id " + taskId2 + " exists");
        }

        if ((task1.getProjectId() == null && task2.getProjectId() != null) || (task1.getParentTaskId() == null && task2.getParentTaskId() != null)) {
            throw new IllegalArgumentException("The specified tasks must both be either tasks or details of tasks, not mixed");
        }

        if (!Objects.equals(task1.getProjectId(), task2.getProjectId())) {
            throw new IllegalArgumentException("Project ids of the specified tasks must be equal");
        }

        if (!Objects.equals(task1.getParentTaskId(), task2.getParentTaskId())) {
            throw new IllegalArgumentException("Parent task ids of the specified tasks must be equal");
        }

        int positionOfTask1 = task1.getPosition();
        task1.setPosition(task2.getPosition());
        task2.setPosition(positionOfTask1);

        tasksDao.save(task1);
        tasksDao.save(task2);
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
