package me.nikoltur.todolist.tasks.da;

import java.util.List;

/**
 * Data-access object for Task-entity.
 *
 * @author Nikolas Turunen
 */
public interface TasksDao {

    /**
     * Returns a list containing all tasks of the specified project.
     *
     * @param projectId Id of the project.
     * @return A list containing all tasks of the specified project.
     */
    public List<Task> getAllOf(int projectId);

    /**
     * Saves the specified task.
     *
     * @param task Task to be saved.
     */
    public void save(Task task);
}
