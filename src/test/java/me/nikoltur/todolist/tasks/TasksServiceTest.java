package me.nikoltur.todolist.tasks;

import java.util.ArrayList;
import java.util.List;
import me.nikoltur.todolist.projects.ProjectDoesNotExistException;
import me.nikoltur.todolist.projects.da.ProjectsDao;
import me.nikoltur.todolist.tasks.da.Task;
import me.nikoltur.todolist.tasks.da.TasksDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.anyInt;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 *
 * @author Nikolas Turunen
 */
public class TasksServiceTest {

    @InjectMocks
    private TasksService tasksService = new TasksServiceImpl();
    @Mock
    private TasksDao tasksDao;
    @Mock
    private ProjectsDao projectsDao;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetTasks() {
        String taskString = "Do this and do that";

        List<Task> tasks = new ArrayList<>();
        Task task = new Task();
        task.setTaskString(taskString);
        tasks.add(task);

        Mockito.doReturn(tasks).when(tasksDao).getAllOf(1);

        Assert.assertEquals("Size should be 1", 1, tasksService.getTasks(1).size());
        Assert.assertEquals("Task string should match", taskString, tasksService.getTasks(1).get(0).getTaskString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetTasksThrowsForZeroId() {
        Mockito.doReturn(new ArrayList<>()).when(tasksDao).getAllOf(anyInt());

        tasksService.getTasks(0);
    }

    @Test
    public void testGetTasksThrowsForNegativeId() {
        Mockito.doReturn(new ArrayList<>()).when(tasksDao).getAllOf(anyInt());

        try {
            tasksService.getTasks(-1);
            Assert.fail();
        } catch (IllegalArgumentException ex) {
        }

        try {
            tasksService.getTasks(-435);
            Assert.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void testCreateTask() {
        int projectId = 1;
        String taskString = "Hello world";

        ArgumentCaptor<Task> argumentCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(argumentCaptor.capture());

        Mockito.doReturn(true).when(projectsDao).exists(projectId);

        tasksService.createTask(projectId, taskString);

        Assert.assertEquals("Saved task should have the specified id", projectId, argumentCaptor.getValue().getProjectId());
        Assert.assertEquals("Saved task should have the specified task string", taskString, argumentCaptor.getValue().getTaskString());
    }

    @Test
    public void testCreateTaskThrowsForIllegalProjectId() {
        String taskString = "Hello world";

        try {
            tasksService.createTask(0, taskString);
            Assert.fail();
        } catch (IllegalArgumentException ex) {
        }

        try {
            tasksService.createTask(-1, taskString);
            Assert.fail();
        } catch (IllegalArgumentException ex) {
        }

        try {
            tasksService.createTask(-12332, taskString);
            Assert.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test(expected = NullPointerException.class)
    public void testCreateTaskThrowsForNullTask() {
        int projectId = 1;
        String taskString = null;

        tasksService.createTask(projectId, taskString);
    }

    @Test(expected = ProjectDoesNotExistException.class)
    public void testCreateTaskThrowsForNonExistingProject() {
        Mockito.doReturn(false).when(projectsDao).exists(anyInt());

        tasksService.createTask(1243, "");
    }
}
