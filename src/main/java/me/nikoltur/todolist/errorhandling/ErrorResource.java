package me.nikoltur.todolist.errorhandling;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Rest-resource to handle error responses.
 *
 * @author Nikolas Turunen
 */
@RestController
public class ErrorResource implements ErrorController {

    private static final String PATH = "/error";
    @Autowired
    private ErrorAttributes errorAttributes;

    @RequestMapping(PATH)
    public ErrorResponse error(HttpServletRequest request, HttpServletResponse response) {

        return new ErrorResponse(getErrorAttributes(request));
    }

    private Map<String, Object> getErrorAttributes(HttpServletRequest request) {
        RequestAttributes requestAttributes = new ServletRequestAttributes(request);
        return errorAttributes.getErrorAttributes(requestAttributes, true);
    }

    @Override
    public String getErrorPath() {
        return PATH;
    }
}
