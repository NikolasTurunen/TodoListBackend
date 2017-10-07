package me.nikoltur.todolist.tasks;

/**
 * Thrown to indicate that a task is not marked as completed.
 *
 * @author Nikolas Turunen
 */
public class TaskNotCompletedException extends RuntimeException {

    public TaskNotCompletedException() {
    }

    public TaskNotCompletedException(String message) {
        super(message);
    }

    public TaskNotCompletedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskNotCompletedException(Throwable cause) {
        super(cause);
    }
}
