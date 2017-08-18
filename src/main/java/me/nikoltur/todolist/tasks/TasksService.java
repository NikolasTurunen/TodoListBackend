package me.nikoltur.todolist.tasks;

import java.util.List;
import me.nikoltur.todolist.tasks.da.Task;

/**
 * Service to access and control tasks.
 *
 * @author Nikolas Turunen
 */
public interface TasksService {

    /**
     * Return a list containing tasks of the specified project.
     *
     * @param projectId Id of the project.
     * @return A list containing tasks of the specified project.
     * @throws IllegalArgumentException Thrown if the specified projectId is negative or zero.
     */
    public List<Task> getTasks(int projectId);

    /**
     * Creates a new task for the specified project.
     *
     * @param projectId Id of the project the task is going to be created for.
     * @param taskString Task.
     * @throws IllegalArgumentException Thrown if the specified projectId is negative or zero.
     * @throws NullPointerException Thrown if the specified task is null.
     */
    public void createTask(int projectId, String taskString);
}