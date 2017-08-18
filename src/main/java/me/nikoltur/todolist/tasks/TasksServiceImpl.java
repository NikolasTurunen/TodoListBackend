package me.nikoltur.todolist.tasks;

import java.util.List;
import javax.transaction.Transactional;
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
}
