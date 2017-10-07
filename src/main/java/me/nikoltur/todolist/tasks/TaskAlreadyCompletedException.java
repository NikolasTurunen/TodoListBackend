package me.nikoltur.todolist.tasks;

/**
 * Thrown to indicate that a task is already completed.
 *
 * @author Nikolas Turunen
 */
public class TaskAlreadyCompletedException extends RuntimeException {

    public TaskAlreadyCompletedException() {
    }

    public TaskAlreadyCompletedException(String message) {
        super(message);
    }

    public TaskAlreadyCompletedException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskAlreadyCompletedException(Throwable cause) {
        super(cause);
    }
}
