package me.nikoltur.todolist.tasks;

/**
 * Thrown to indicate that a task does not have a parent.
 *
 * @author Nikolas Turunen
 */
public class TaskDoesNotHaveParentException extends RuntimeException {

    public TaskDoesNotHaveParentException() {
    }

    public TaskDoesNotHaveParentException(String message) {
        super(message);
    }

    public TaskDoesNotHaveParentException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskDoesNotHaveParentException(Throwable cause) {
        super(cause);
    }
}
