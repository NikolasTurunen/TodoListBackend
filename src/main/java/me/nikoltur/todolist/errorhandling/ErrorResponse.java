package me.nikoltur.todolist.errorhandling;

import java.util.Map;

/**
 * Error response.
 *
 * @author Nikolas Turunen
 */
public class ErrorResponse {

    private final int status;
    private final String message;

    public ErrorResponse(Map<String, Object> errorAttributes) {
        this.status = (int) errorAttributes.get("status");
        this.message = (String) errorAttributes.get("message");
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
