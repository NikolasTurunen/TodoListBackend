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
     * Return a list containing tasks of the specified project ordered by their position.
     *
     * @param projectId Id of the project.
     * @return A list containing tasks of the specified project ordered by their position.
     * @throws ProjectDoesNotExistException Thrown if a project with the specified projectId does not exist.
     * @throws IllegalArgumentException Thrown if the specified projectId is negative or zero.
     */
    public List<Task> getTasks(int projectId);

    /**
     * Creates a new task for the specified project.
     *
     * @param projectId Id of the project the task is going to be created for.
     * @param taskString Task.
     * @throws ProjectDoesNotExistException Thrown if a project with the specified projectId does not exist.
     * @throws IllegalArgumentException Thrown if the specified projectId is negative or zero.
     * @throws NullPointerException Thrown if the specified task is null.
     */
    public void createTask(int projectId, String taskString);

    /**
     * Removes the specified task.
     *
     * @param taskId Id of the task to be removed.
     * @throws TaskDoesNotExistException Thrown if no task with the specified taskId exists.
     * @throws IllegalArgumentException Thrown if the specified taskId is negative or zero.
     */
    public void removeTask(int taskId);

    /**
     * Edits the specified task to the specified newTask.
     *
     * @param taskId Id of the task to be edited.
     * @param newTask New task string to be the new task.
     * @throws TaskDoesNotExistException Thrown if no task with the specified taskId exists.
     * @throws IllegalArgumentException Thrown if the specified taskId is negative or zero.
     * @throws NullPointerException Thrown if the specified task is null.
     */
    public void editTask(int taskId, String newTask);

    /**
     * Creates a detail for the specified task.
     * The created detail is a task with a parent so it can be controlled with the {@link #editTask(int, String) editTask} and {@link #removeTask(int) removeTask} methods.
     *
     * @param taskId Id of the task.
     * @param detail Detail for the task.
     * @throws TaskDoesNotExistException Thrown if no task with the specified taskId exists.
     * @throws IllegalArgumentException Thrown if the specified taskId is negative or zero.
     * @throws NullPointerException Thrown if the specified detail is null.
     */
    public void createDetail(int taskId, String detail);

    /**
     * Swaps the positions of the specified tasks.
     *
     * @param taskId Id of the first task.
     * @param taskId2 Id of the second task.
     * @throws TaskDoesNotExistException Thrown if no task with the specified taskId or taskId2 exists.
     * @throws IllegalArgumentException Thrown if the specified taskId or taskId2 is negative or zero.
     * Or if the specified taskId equals the specified taskId2.
     * Or if the project ids of the tasks are not equal.
     * Or if the parent task ids of the tasks are not equal.
     */
    public void swapPositionsOfTasks(int taskId, int taskId2);

    /**
     * Sets the specified task as completed.
     *
     * @param taskId Id of the task.
     * @throws TaskAlreadyCompletedException Thrown if the specified task is already completed.
     * @throws TaskDoesNotExistException Thrown if no task with the specified taskId exists.
     * @throws IllegalArgumentException Thrown if the specified taskId is negative or zero.
     */
    public void completeTask(int taskId);

    /**
     * Sets the specified task as not completed.
     *
     * @param taskId Id of the task.
     * @throws TaskNotCompletedException Thrown if the specified task is not completed.
     * @throws TaskDoesNotExistException Thrown if no task with the specified taskId exists.
     * @throws IllegalArgumentException Thrown if the specified taskId is negative or zero.
     */
    public void uncompleteTask(int taskId);

    /**
     * Changes the parent task id of the specified task to the specified new parent task id.
     *
     * @param taskId Id of the task to be moved.
     * @param newParentTaskId New parent task id for the task to be moved.
     * @throws TaskDoesNotExistException Thrown if the specified task to be moved or the new parent task does not exist.
     * @throws IllegalArgumentException Thrown if the specified taskId or newParentTaskId is negative or zero.
     * Or if the taskId is equal to newParentTaskId.
     * Or if the parent task id of the task to be moved is equal to the specified newParentTaskId.
     */
    public void moveTask(int taskId, int newParentTaskId);
}
