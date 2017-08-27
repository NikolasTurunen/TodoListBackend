package me.nikoltur.todolist.tasks;

import java.util.ArrayList;
import java.util.List;
import me.nikoltur.todolist.Application;
import me.nikoltur.todolist.tasks.da.Task;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author Nikolas Turunen
 */
@RunWith(SpringRunner.class)
@TestPropertySource("classpath:application.properties")
@ContextConfiguration(classes = Application.class)
public class TasksResourceTest {

    @InjectMocks
    private TasksResource tasksResource;
    @Mock
    private TasksService tasksService;

    @Before
    public void initMocks() {
        tasksResource = new TasksResource();

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetTasks() {
        int projectId = 1;
        List<Task> tasks = new ArrayList<>();

        Mockito.doReturn(tasks).when(tasksService).getTasks(projectId);

        Assert.assertSame("Returned list should be the same as the list returned from the service", tasks, tasksResource.getTasks(projectId));
    }

    @Test
    public void testCreateTask() {
        int projectId = 1;
        String task = "do this";

        tasksResource.createTask(projectId, task);

        Mockito.verify(tasksService, times(1)).createTask(projectId, task);
    }

    @Test
    public void testRemoveTask() {
        int taskId = 1;

        tasksResource.removeTask(taskId);

        Mockito.verify(tasksService, times(1)).removeTask(taskId);
    }

    @Test
    public void testEditTask() {
        int taskId = 1;
        String newTask = "Do this instead!";

        tasksResource.editTask(taskId, newTask);

        Mockito.verify(tasksService, times(1)).editTask(taskId, newTask);
    }

    @Test
    public void testCreateDetail() {
        int taskId = 1;
        String taskDetail = "Detail";

        tasksResource.createDetail(taskId, taskDetail);

        Mockito.verify(tasksService, times(1)).createDetail(taskId, taskDetail);
    }

    @Test
    public void testSwapPositionsOfTasks() {
        int taskId = 1;
        int taskId2 = 2;

        tasksResource.swapPositionsOfTasks(taskId, taskId2);

        Mockito.verify(tasksService, times(1)).swapPositionsOfTasks(taskId, taskId2);
    }
}
