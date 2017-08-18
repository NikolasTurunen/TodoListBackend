package me.nikoltur.todolist.projects;

/**
 * Thrown to indicate that a project does not exist.
 *
 * @author Nikolas Turunen
 */
public class ProjectDoesNotExistException extends RuntimeException {

    public ProjectDoesNotExistException() {
    }

    public ProjectDoesNotExistException(String message) {
        super(message);
    }

    public ProjectDoesNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProjectDoesNotExistException(Throwable cause) {
        super(cause);
    }
}
