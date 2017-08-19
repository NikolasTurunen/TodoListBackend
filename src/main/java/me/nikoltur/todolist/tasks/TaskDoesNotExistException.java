package me.nikoltur.todolist.tasks;

/**
 * Thrown to indicate that a task does not exist.
 *
 * @author Nikolas Turunen
 */
public class TaskDoesNotExistException extends RuntimeException {

    public TaskDoesNotExistException() {
    }

    public TaskDoesNotExistException(String message) {
        super(message);
    }

    public TaskDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public TaskDoesNotExistException(Throwable cause) {
        super(cause);
    }
}
