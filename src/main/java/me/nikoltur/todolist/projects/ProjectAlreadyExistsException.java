package me.nikoltur.todolist.projects;

/**
 * Thrown to indicate that a project already exists.
 *
 * @author Nikolas Turunen
 */
public class ProjectAlreadyExistsException extends RuntimeException {

    public ProjectAlreadyExistsException() {
    }

    public ProjectAlreadyExistsException(String message) {
        super(message);
    }

    public ProjectAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProjectAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
