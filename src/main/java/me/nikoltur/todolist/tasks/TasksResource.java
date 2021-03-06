package me.nikoltur.todolist.tasks;

import java.util.List;
import me.nikoltur.todolist.RestControllerConfiguration;
import me.nikoltur.todolist.tasks.da.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rest-resource to access and manage tasks.
 *
 * @author Nikolas Turunen
 */
@RestController
@RequestMapping(RestControllerConfiguration.CONTEXT_PATH)
@CrossOrigin
public class TasksResource {

    private static final String BASE_PATH = "/tasks";
    @Autowired
    private TasksService tasksService;

    /**
     * Returns a list containing tasks of the specified project ordered by their position.
     *
     * @param projectId Id of the project.
     * @return A list containing tasks of the specified project ordered by their position.
     */
    @GetMapping(BASE_PATH)
    public List<Task> getTasks(@RequestParam("projectId") int projectId) {
        return tasksService.getTasks(projectId);
    }

    /**
     * Creates a new task for the specified project.
     *
     * @param projectId Id of the project.
     * @param task Task as a string.
     */
    @PostMapping(BASE_PATH + "/create")
    public void createTask(@RequestParam("projectId") int projectId, @RequestParam("task") String task) {
        tasksService.createTask(projectId, task);
    }

    /**
     * Removes the specified task.
     *
     * @param taskId Id of the task to be removed.
     */
    @PostMapping(BASE_PATH + "/remove")
    public void removeTask(@RequestParam("taskId") int taskId) {
        tasksService.removeTask(taskId);
    }

    /**
     * Edits the specified task to the specified newTask.
     *
     * @param taskId Id of the task to be edited.
     * @param newTask New task string to be the new task.
     */
    @PostMapping(BASE_PATH + "/edit")
    public void editTask(@RequestParam("taskId") int taskId, @RequestParam("newTask") String newTask) {
        tasksService.editTask(taskId, newTask);
    }

    /**
     * Creates a detail for the specified task.
     *
     * @param taskId Id of the task.
     * @param detail Detail for the task.
     */
    @PostMapping(BASE_PATH + "/createdetail")
    public void createDetail(@RequestParam("taskId") int taskId, @RequestParam("detail") String detail) {
        tasksService.createDetail(taskId, detail);
    }

    /**
     * Swaps the positions of the specified tasks.
     *
     * @param taskId Id of the first task.
     * @param taskId2 Id of the second task.
     */
    @PostMapping(BASE_PATH + "/swappositions")
    public void swapPositionsOfTasks(@RequestParam("taskId") int taskId, @RequestParam("taskId2") int taskId2) {
        tasksService.swapPositionsOfTasks(taskId, taskId2);
    }

    /**
     * Sets the specified task as completed.
     *
     * @param taskId Id of the task.
     */
    @PostMapping(BASE_PATH + "/complete")
    public void completeTask(@RequestParam("taskId") int taskId) {
        tasksService.completeTask(taskId);
    }

    /**
     * Sets the specified task as not completed.
     *
     * @param taskId Id of the task.
     */
    @PostMapping(BASE_PATH + "/uncomplete")
    public void uncompleteTask(@RequestParam("taskId") int taskId) {
        tasksService.uncompleteTask(taskId);
    }

    /**
     * Moves the specified task to be a detail of the specified new parent task.
     *
     * @param taskId Id of the task to be moved.
     * @param newParentTaskId Id of the new parent task for the task to be moved.
     * @param newProjectId Id of the new project id for the task if the newParentTaskId is null. Null to keep the project id unchanged.
     */
    @PostMapping(BASE_PATH + "/move")
    public void moveTask(@RequestParam("taskId") int taskId, @RequestParam("newParentTaskId") Integer newParentTaskId, @RequestParam(name = "newProjectId", required = false) Integer newProjectId) {
        tasksService.moveTask(taskId, newParentTaskId, newProjectId);
    }
}
