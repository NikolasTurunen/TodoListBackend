package me.nikoltur.todolist.tasks;

import java.util.List;
import java.util.Objects;
import javax.transaction.Transactional;
import me.nikoltur.todolist.projects.ProjectDoesNotExistException;
import me.nikoltur.todolist.projects.da.Project;
import me.nikoltur.todolist.projects.da.ProjectsDao;
import me.nikoltur.todolist.tasks.da.Task;
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
public class TasksServiceImpl implements TasksService {

    @Autowired
    private TasksDao tasksDao;
    @Autowired
    private ProjectsDao projectsDao;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public List<Task> getTasks(int projectId) {
        validateProjectId(projectId);

        Project project = projectsDao.getById(projectId);
        if (project == null) {
            throw new ProjectDoesNotExistException("No project with the id " + projectId + " exists");
        }

        return tasksDao.getAllOf(projectId);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public synchronized void createTask(int projectId, String taskString) {
        validateProjectId(projectId);
        validateTaskString(taskString);

        Project project = projectsDao.getById(projectId);
        if (project == null) {
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
    public synchronized void removeTask(int taskId) {
        validateTaskId(taskId);

        Task task = tasksDao.getById(taskId);

        if (task == null) {
            throw new TaskDoesNotExistException("No task with id " + taskId + " exists");
        }

        tasksDao.remove(task);

        decrementPositionsOfTasksWithHigherPosition(task.getPosition(), getTasksWith(task));
    }

    /**
     * Gets a list containing tasks that are under the same project or have the same parent task.
     *
     * @param task Task.
     * @return A list containing tasks that are under the same project or have the same parent task.
     */
    private List<Task> getTasksWith(Task task) {
        List<Task> remainingTasks;
        if (task.getParentTaskId() == null) {
            // If parent task id is null it means that the task is at the top level.
            remainingTasks = tasksDao.getAllOf(task.getProjectId());
        } else {
            // Otherwise the task is a task detail. Get the details of the parent task to get all the details on the same level.
            remainingTasks = tasksDao.getById(task.getParentTaskId()).getDetails();
        }

        return remainingTasks;
    }

    /**
     * Decrements positions of tasks in the specified list of tasks that have a higher position than the specified positionThreshold.
     *
     * @param positionThreshold Position threshold.
     * @param tasks Tasks to be iterated.
     */
    private void decrementPositionsOfTasksWithHigherPosition(int positionThreshold, List<Task> tasks) {
        for (Task remainingTask : tasks) {
            if (remainingTask.getPosition() > positionThreshold) {
                remainingTask.setPosition(remainingTask.getPosition() - 1);
                tasksDao.save(remainingTask);
            }
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public synchronized void editTask(int taskId, String newTask) {
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
    public synchronized void createDetail(int taskId, String detail) {
        validateTaskId(taskId);
        validateTaskString(detail);

        Task parentTask = tasksDao.getById(taskId);
        if (parentTask == null) {
            throw new TaskDoesNotExistException("No task with id " + taskId + " exists");
        }

        int position = parentTask.getDetails().size();

        Task task = new Task();
        task.setProjectId(parentTask.getProjectId());
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
    public synchronized void swapPositionsOfTasks(int taskId, int taskId2) {
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

        if (!Objects.equals(task1.getProjectId(), task2.getProjectId())) {
            throw new IllegalArgumentException("Project ids of the specified tasks must be equal");
        }

        if (!Objects.equals(task1.getParentTaskId(), task2.getParentTaskId())) {
            throw new IllegalArgumentException("Parent task ids of the specified tasks must be equal");
        }

        boolean task2IsPreviousFromTask1 = task2.getPosition() == task1.getPosition() + 1;
        boolean task2IsNextFromTask1 = task2.getPosition() == task1.getPosition() - 1;
        if (!task2IsPreviousFromTask1 && !task2IsNextFromTask1) {
            throw new IllegalArgumentException("The specified second task must be either previous or next from the specified first task");
        }

        int positionOfTask1 = task1.getPosition();
        task1.setPosition(task2.getPosition());
        task2.setPosition(positionOfTask1);

        tasksDao.save(task1);
        tasksDao.save(task2);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public synchronized void completeTask(int taskId) {
        validateTaskId(taskId);

        Task task = tasksDao.getById(taskId);
        if (task == null) {
            throw new TaskDoesNotExistException("No task with id " + taskId + " exists");
        }

        if (task.isCompleted()) {
            throw new TaskAlreadyCompletedException("The specified task is already completed");
        }

        task.setCompleted(true);
        tasksDao.save(task);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public synchronized void uncompleteTask(int taskId) {
        validateTaskId(taskId);

        Task task = tasksDao.getById(taskId);
        if (task == null) {
            throw new TaskDoesNotExistException("No task with id " + taskId + " exists");
        }

        if (!task.isCompleted()) {
            throw new TaskNotCompletedException("The specified task is not marked as completed");
        }

        task.setCompleted(false);
        tasksDao.save(task);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public synchronized void moveTask(int taskId, Integer newParentTaskId, Integer newProjectId) {
        validateTaskId(taskId);

        Task task = tasksDao.getById(taskId);
        if (task == null) {
            throw new TaskDoesNotExistException("No task with id " + taskId + " exists");
        }

        int currentProjectId = task.getProjectId();

        int newPosition;
        if (newParentTaskId != null) {
            if (taskId == newParentTaskId) {
                throw new IllegalArgumentException("Cannot move task to be a detail of itself");
            }

            validateTaskId(newParentTaskId);
            Task newParentTask = tasksDao.getById(newParentTaskId);
            if (newParentTask == null) {
                throw new TaskDoesNotExistException("No task with id " + newParentTaskId + " exists");
            }

            if (isTaskLowerInHierarchy(task, newParentTask)) {
                throw new IllegalArgumentException("The new parent task cannot be a detail of the task lower in the hierarchy");
            }

            if (task.getParentTaskId() != null && task.getParentTaskId() == (int) newParentTaskId) {
                throw new IllegalArgumentException("Task is already a detail of the new parent task");
            }

            task.setProjectId(newParentTask.getProjectId());

            updateProjectIdsOfTasksLowerInHierarchy(task, newParentTask.getProjectId());

            newPosition = newParentTask.getDetails().size();
        } else {
            if (newProjectId == null && task.getParentTaskId() == null) {
                throw new TaskDoesNotHaveParentException("Task does not have a parent task to clear");
            }

            if (newProjectId != null) {
                Project newProject = projectsDao.getById(newProjectId);
                if (newProject == null) {
                    throw new ProjectDoesNotExistException("No project with the id " + newProjectId + " exists");
                }

                List<Task> tasksOfNewProject = tasksDao.getAllOf(newProjectId);
                newPosition = tasksOfNewProject.size();

                task.setProjectId(newProjectId);

                updateProjectIdsOfTasksLowerInHierarchy(task, newProjectId);
            } else {
                List<Task> tasksOfProject = tasksDao.getAllOf(task.getProjectId());
                newPosition = tasksOfProject.size();
            }
        }

        if (task.getParentTaskId() != null) {
            decrementPositionsOfTasksWithHigherPosition(task.getPosition(), tasksDao.getById(task.getParentTaskId()).getDetails());
        } else {
            decrementPositionsOfTasksWithHigherPosition(task.getPosition(), tasksDao.getAllOf(currentProjectId));
        }

        task.setParentTaskId(newParentTaskId);
        task.setPosition(newPosition);

        tasksDao.save(task);
    }

    /**
     * Searches if the specified taskSearched is a detail of the specified task but lower in the hierarchy.
     *
     * @param task Task to start the search from.
     * @param taskSearched Task to be searched lower in the hierarchy.
     * @return true if the specified taskSearched is a detail of the specified task but lower in the hierarchy.
     */
    private boolean isTaskLowerInHierarchy(Task task, Task taskSearched) {
        for (Task detail : task.getDetails()) {
            if (taskSearched.getId() == detail.getId()) {
                return true;
            }

            if (isTaskLowerInHierarchy(detail, taskSearched)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Updates project ids of tasks lower in the hierarchy of the specified task to the specified new project id.
     *
     * @param task Task.
     * @param newProjectId New project id for the tasks.
     */
    private void updateProjectIdsOfTasksLowerInHierarchy(Task task, int newProjectId) {
        for (Task detail : task.getDetails()) {
            detail.setProjectId(newProjectId);
            tasksDao.save(detail);

            updateProjectIdsOfTasksLowerInHierarchy(detail, newProjectId);
        }
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
