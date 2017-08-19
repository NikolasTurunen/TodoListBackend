package me.nikoltur.todolist.projects;

/**
 * Thrown to indicate that a project still has tasks referencing to it.
 *
 * @author Nikolas Turunen
 */
public class ProjectHasTasksException extends RuntimeException {

    public ProjectHasTasksException() {
    }

    public ProjectHasTasksException(String message) {
        super(message);
    }

    public ProjectHasTasksException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProjectHasTasksException(Throwable cause) {
        super(cause);
    }
}
