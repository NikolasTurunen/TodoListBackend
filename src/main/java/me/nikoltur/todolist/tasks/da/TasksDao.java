package me.nikoltur.todolist.tasks.da;

import java.util.List;

/**
 * Data-access object for Task-entity.
 *
 * @author Nikolas Turunen
 */
public interface TasksDao {

    /**
     * Returns a list containing all tasks of the specified project ordered by their position.
     *
     * @param projectId Id of the project.
     * @return A list containing all tasks of the specified project ordered by their position.
     */
    public List<Task> getAllOf(int projectId);

    /**
     * Returns the task with the specified taskId;
     *
     * @param taskId Id of the task.
     * @return The task with the specified taskId;
     */
    public Task getById(int taskId);

    /**
     * Saves the specified task.
     *
     * @param task Task to be saved.
     */
    public void save(Task task);

    /**
     * Removes the specified task.
     *
     * @param task Task to be removed.
     */
    public void remove(Task task);
}
