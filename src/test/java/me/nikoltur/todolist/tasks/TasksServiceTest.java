package me.nikoltur.todolist.tasks;

import java.util.ArrayList;
import java.util.List;
import me.nikoltur.todolist.projects.ProjectDoesNotExistException;
import me.nikoltur.todolist.projects.da.Project;
import me.nikoltur.todolist.projects.da.ProjectsDao;
import me.nikoltur.todolist.tasks.da.Task;
import me.nikoltur.todolist.tasks.da.TasksDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
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
        int projectId = 1;
        String taskString = "Do this and do that";

        List<Task> tasks = new ArrayList<>();
        Task task = new Task();
        task.setTaskString(taskString);
        tasks.add(task);

        Project project = new Project();
        Mockito.doReturn(project).when(projectsDao).getById(projectId);

        Mockito.doReturn(tasks).when(tasksDao).getAllOf(projectId);

        Assert.assertEquals("Size should be 1", 1, tasksService.getTasks(1).size());
        Assert.assertEquals("Task string should match", taskString, tasksService.getTasks(1).get(0).getTaskString());
    }

    @Test(expected = ProjectDoesNotExistException.class)
    public void testGetTasksThrowsForNonExistingProject() {
        int projectId = 1;

        Mockito.doReturn(null).when(tasksDao).getAllOf(projectId);

        tasksService.getTasks(projectId);
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
        Integer projectId = 1;
        String taskString = "Hello world";

        ArgumentCaptor<Task> argumentCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(argumentCaptor.capture());

        Project project = new Project();
        Mockito.doReturn(project).when(projectsDao).getById(projectId);

        tasksService.createTask(projectId, taskString);

        Assert.assertEquals("Saved task should have the specified id", projectId, argumentCaptor.getValue().getProjectId());
        Assert.assertEquals("Saved task should have the specified task string", taskString, argumentCaptor.getValue().getTaskString());
        Assert.assertNull("Parent task id of the saved task should be set to null", argumentCaptor.getValue().getParentTaskId());
        Assert.assertFalse("Completion of the saved task should be false", argumentCaptor.getValue().isCompleted());
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
        Mockito.doReturn(null).when(projectsDao).getById(anyInt());

        tasksService.createTask(1243, "");
    }

    @Test
    public void testRemoveTask() {
        int taskId = 1;

        ArgumentCaptor<Task> argumentCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).remove(argumentCaptor.capture());

        Task task = new Task();
        task.setProjectId(123);
        task.setTaskString("Do this and that");

        Mockito.doReturn(task).when(tasksDao).getById(taskId);

        tasksService.removeTask(taskId);
        Mockito.verify(tasksDao, times(1)).remove(anyObject());

        Assert.assertSame("Removed task should be the same that was returned from getById", task, argumentCaptor.getValue());
    }

    @Test
    public void testRemoveTaskThrowsForIllegalId() {
        try {
            tasksService.removeTask(0);
            Assert.fail("Zero task id should throw an exception");
        } catch (IllegalArgumentException ex) {

        }

        try {
            tasksService.removeTask(-1);
            Assert.fail("Negative task id should throw an exception");
        } catch (IllegalArgumentException ex) {

        }

        try {
            tasksService.removeTask(-354);
            Assert.fail("Negative task id should throw an exception");
        } catch (IllegalArgumentException ex) {

        }
    }

    @Test(expected = TaskDoesNotExistException.class)
    public void testRemoveTaskThatDoesNotExistThrows() {
        int taskId = 1;
        Mockito.doReturn(null).when(tasksDao).getById(taskId);

        tasksService.removeTask(taskId);
    }

    @Test
    public void testEditTask() {
        int taskId = 1;
        String newTask = "Do this now!";

        Task task = new Task();
        task.setProjectId(1);
        task.setTaskString("Test task");

        Mockito.doReturn(task).when(tasksDao).getById(taskId);

        ArgumentCaptor<Task> argumentCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(argumentCaptor.capture());

        tasksService.editTask(taskId, newTask);

        Mockito.verify(tasksDao, times(1)).save(anyObject());
        Assert.assertSame("Saved task should be the same as the task to be edited", task, argumentCaptor.getValue());
        Assert.assertEquals("Saved task should have the specified newTask", newTask, argumentCaptor.getValue().getTaskString());
    }

    @Test
    public void testEditTaskThrowsForIllegalId() {
        String newTask = "Do this now!";

        try {
            tasksService.editTask(0, newTask);
            Assert.fail("Zero task id should throw an exception");
        } catch (IllegalArgumentException ex) {
        }

        try {
            tasksService.editTask(-1, newTask);
            Assert.fail("Negative task id should throw an exception");
        } catch (IllegalArgumentException ex) {
        }

        try {
            tasksService.editTask(-354, newTask);
            Assert.fail("Negative task id should throw an exception");
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test(expected = NullPointerException.class)
    public void testEditTaskThrowsForNullNewTask() {
        int taskId = 1;

        Task task = new Task();
        task.setTaskString("Do that");
        Mockito.doReturn(task).when(tasksDao).getById(taskId);

        tasksService.editTask(taskId, null);
    }

    @Test(expected = TaskDoesNotExistException.class)
    public void testEditTaskThrowsForNonExistingTask() {
        int taskId = 1;

        Mockito.doReturn(null).when(tasksDao).getById(taskId);

        tasksService.editTask(taskId, "Task to do");
    }

    @Test
    public void testCreateDetail() {
        Integer parentTaskId = 1;
        String detailString = "Detail";

        ArgumentCaptor<Task> argumentCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(argumentCaptor.capture());

        Task task = Mockito.mock(Task.class);
        Mockito.doReturn(task).when(tasksDao).getById(parentTaskId);

        tasksService.createDetail(parentTaskId, detailString);

        Mockito.verify(tasksDao, times(1)).save(anyObject());
        Assert.assertEquals("Parent task id of the saved task should be the specified task id", parentTaskId, argumentCaptor.getValue().getParentTaskId());
        Assert.assertNull("Project id of the saved task should be null", argumentCaptor.getValue().getProjectId());
        Assert.assertEquals("Detail of the saved task should be the specified detail", detailString, argumentCaptor.getValue().getTaskString());
    }

    @Test
    public void testCreateDetailThrowsForIllegalTaskId() {
        String newTask = "Do this now!";

        try {
            tasksService.createDetail(0, newTask);
            Assert.fail("Zero task id should throw an exception");
        } catch (IllegalArgumentException ex) {
        }

        try {
            tasksService.createDetail(-1, newTask);
            Assert.fail("Negative task id should throw an exception");
        } catch (IllegalArgumentException ex) {
        }

        try {
            tasksService.createDetail(-354, newTask);
            Assert.fail("Negative task id should throw an exception");
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test(expected = NullPointerException.class)
    public void testCreateDetailThrowsForNullDetail() {
        tasksService.createDetail(1, null);
    }

    @Test(expected = TaskDoesNotExistException.class)
    public void testCreateDetailThrowsForNonExistingParentTask() {
        int parentTaskId = 1;

        Mockito.doReturn(null).when(tasksDao).getById(parentTaskId);

        tasksService.createDetail(parentTaskId, "Detail");
    }

    @Test
    public void testSwapPositionsOfTasks() {
        int positionOfTask = 1;
        int positionOfTask2 = 2;

        int taskId = 1;
        int taskId2 = 2;

        Task task = new Task();
        task.setPosition(positionOfTask);

        Task task2 = new Task();
        task2.setPosition(positionOfTask2);

        Mockito.doReturn(task).when(tasksDao).getById(taskId);
        Mockito.doReturn(task2).when(tasksDao).getById(taskId2);

        ArgumentCaptor<Task> argumentCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(argumentCaptor.capture());

        tasksService.swapPositionsOfTasks(taskId, taskId2);

        Mockito.verify(tasksDao, times(2)).save(anyObject());
        for (Task argument : argumentCaptor.getAllValues()) {
            if (argument == task) {
                Assert.assertEquals("Position of saved task should be the position of task2 after swap", positionOfTask2, argument.getPosition());
            } else if (argument == task2) {
                Assert.assertEquals("Position of saved task2 should be the position of task after swap", positionOfTask, argument.getPosition());
            } else {
                Assert.fail("Save should not have been called with an unexpected argument");
            }
        }
    }

    @Test
    public void testSwapPositionsOfTasksThrowsForInvalidId() {
        int validTaskId = 1;

        try {
            tasksService.swapPositionsOfTasks(0, validTaskId);
            Assert.fail("Zero task id should throw an exception");
        } catch (IllegalArgumentException ex) {
        }

        try {
            tasksService.swapPositionsOfTasks(-1, validTaskId);
            Assert.fail("Negative task id should throw an exception");
        } catch (IllegalArgumentException ex) {
        }

        try {
            tasksService.swapPositionsOfTasks(-354, validTaskId);
            Assert.fail("Negative task id should throw an exception");
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSwapPositionsOfTasksThrowsForEqualIds() {
        int taskId = 1;
        tasksService.swapPositionsOfTasks(taskId, taskId);
    }

    @Test
    public void testSwapPositionsOfTasksThrowsForNonExistingTask() {
        int taskId = 1;
        int taskId2 = 2;

        Task task = new Task();

        Mockito.doReturn(null).when(tasksDao).getById(taskId);
        Mockito.doReturn(task).when(tasksDao).getById(taskId2);
        try {
            tasksService.swapPositionsOfTasks(taskId, taskId2);
            Assert.fail("Should throw TaskDoesNotExistException if the first task does not exist");
        } catch (TaskDoesNotExistException ex) {
        }

        Mockito.doReturn(task).when(tasksDao).getById(taskId);
        Mockito.doReturn(null).when(tasksDao).getById(taskId2);
        try {
            tasksService.swapPositionsOfTasks(taskId, taskId2);
            Assert.fail("Should throw TaskDoesNotExistException if the second task does not exist");
        } catch (TaskDoesNotExistException ex) {
        }

        Mockito.doReturn(null).when(tasksDao).getById(taskId);
        Mockito.doReturn(null).when(tasksDao).getById(taskId2);
        try {
            tasksService.swapPositionsOfTasks(taskId, taskId2);
            Assert.fail("Should throw TaskDoesNotExistException if neither task exists");
        } catch (TaskDoesNotExistException ex) {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSwapPositionsOfTasksThrowsForTasksOfDifferentProjects() {
        int projectId = 1;
        int projectId2 = 2;

        int taskId = 1;
        int taskId2 = 2;

        Task task = new Task();
        task.setProjectId(projectId);

        Task task2 = new Task();
        task2.setProjectId(projectId2);

        Mockito.doReturn(task).when(tasksDao).getById(taskId);
        Mockito.doReturn(task2).when(tasksDao).getById(taskId2);

        tasksService.swapPositionsOfTasks(taskId, taskId2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSwapPositionsOfTasksThrowsForTaskDetailsOfDifferentParentTasks() {
        int parentTaskId = 1;
        int parentTaskId2 = 2;

        int taskId = 1;
        int taskId2 = 2;

        Task task = new Task();
        task.setProjectId(null);
        task.setParentTaskId(parentTaskId);

        Task task2 = new Task();
        task2.setProjectId(null);
        task2.setParentTaskId(parentTaskId2);

        Mockito.doReturn(task).when(tasksDao).getById(taskId);
        Mockito.doReturn(task2).when(tasksDao).getById(taskId2);

        tasksService.swapPositionsOfTasks(taskId, taskId2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSwapPositionsOfTasksThrowsForMixedTaskDetailAndTask() {
        int taskId = 1;
        int taskId2 = 2;

        Task task = new Task();
        task.setProjectId(5);

        Task task2 = new Task();
        task2.setProjectId(null);
        task2.setParentTaskId(10);

        Mockito.doReturn(task).when(tasksDao).getById(taskId);
        Mockito.doReturn(task2).when(tasksDao).getById(taskId2);

        tasksService.swapPositionsOfTasks(taskId, taskId2);
    }

    @Test
    public void testSwapPositionsOfTasksThrowsForOtherThanPreviousOrNext() {
        int taskId1 = 1;
        int taskId2 = 2;

        Task task1 = new Task();
        task1.setPosition(1);

        Task task2 = new Task();
        task2.setPosition(3);

        Mockito.doReturn(task1).when(tasksDao).getById(taskId1);
        Mockito.doReturn(task2).when(tasksDao).getById(taskId2);

        try {
            tasksService.swapPositionsOfTasks(taskId1, taskId2);
            Assert.fail("Should throw IllegalArgumentException if task2 is not previous or next from task1");
        } catch (IllegalArgumentException ex) {
        }

        try {
            tasksService.swapPositionsOfTasks(taskId2, taskId1);
            Assert.fail("Should throw IllegalArgumentException if task1 is not previous or next from task2");
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test
    public void testCreateTaskSetsCorrectPosition() {
        String projectName = "Name";
        String taskString = "Task";
        int projectId = 1;

        Project project = new Project();
        project.setName(projectName);
        project.setPosition(0);

        Mockito.doReturn(project).when(projectsDao).getById(projectId);

        ArgumentCaptor<Task> argumentCaptorForFirstTask = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(argumentCaptorForFirstTask.capture());

        Mockito.doReturn(new ArrayList<>()).when(tasksDao).getAllOf(projectId);

        tasksService.createTask(projectId, taskString);
        Assert.assertEquals("Position of the created task should be set to 0", 0, argumentCaptorForFirstTask.getValue().getPosition());

        ArgumentCaptor<Task> argumentCaptorForSecondTask = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(argumentCaptorForSecondTask.capture());

        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task());

        Mockito.doReturn(tasks).when(tasksDao).getAllOf(projectId);

        tasksService.createTask(projectId, taskString);
        Assert.assertEquals("Position of the created task should be set to 1", 1, argumentCaptorForSecondTask.getValue().getPosition());
    }

    @Test
    public void testCreateDetailSetsCorrectPosition() {
        int parentTaskId = 1;

        Task parentTask = Mockito.spy(new Task());
        Mockito.doReturn(parentTask).when(tasksDao).getById(parentTaskId);

        Mockito.when(parentTask.getDetails()).thenReturn(new ArrayList<>());

        ArgumentCaptor<Task> argumentCaptorForFirstDetail = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(argumentCaptorForFirstDetail.capture());

        tasksService.createDetail(parentTaskId, "Detail");

        Assert.assertEquals("Position of the created detail should be set to 0", 0, argumentCaptorForFirstDetail.getValue().getPosition());

        List<Task> details = new ArrayList<>();
        details.add(new Task());

        Mockito.when(parentTask.getDetails()).thenReturn(details);

        ArgumentCaptor<Task> argumentCaptorForSecondDetail = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(argumentCaptorForSecondDetail.capture());

        tasksService.createDetail(parentTaskId, "Detail2");

        Assert.assertEquals("Position of the created detail should be set to 1", 1, argumentCaptorForSecondDetail.getValue().getPosition());
    }

    @Test
    public void testRemoveTaskDecrementsPositionsOfTasksWithHigherPosition() {
        int projectId = 1;
        int taskId = 1;

        int task3Position = 2;
        int task4Position = 3;

        Task taskToRemove = new Task();
        taskToRemove.setProjectId(projectId);
        taskToRemove.setPosition(1);

        List<Task> tasks = new ArrayList<>();
        Task task1 = new Task();
        task1.setProjectId(projectId);
        task1.setPosition(0);
        tasks.add(task1);
        Task task3 = new Task();
        task3.setProjectId(projectId);
        task3.setPosition(task3Position);
        tasks.add(task3);
        Task task4 = new Task();
        task4.setProjectId(projectId);
        task4.setPosition(task4Position);
        tasks.add(task4);

        Mockito.doReturn(tasks).when(tasksDao).getAllOf(projectId);

        Mockito.doReturn(taskToRemove).when(tasksDao).getById(taskId);

        ArgumentCaptor<Task> argumentCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(argumentCaptor.capture());

        tasksService.removeTask(taskId);

        Mockito.verify(tasksDao, times(2)).save(anyObject());

        for (Task argument : argumentCaptor.getAllValues()) {
            if (argument == task3) {
                Assert.assertEquals("Position of task3 should be updated to 1", 1, argument.getPosition());
            } else if (argument == task4) {
                Assert.assertEquals("Position of task4 should be updated to 2", 2, argument.getPosition());
            } else {
                Assert.fail("Unexpected tasks should not be saved");
            }
        }
    }

    @Test
    public void testRemoveTaskDecrementsPositionsOfOtherDetailsWithHigherPosition() {
        int parentTaskId = 1;
        int taskIdOfDetailToRemove = 10;

        int detail3Position = 2;
        int detail4Position = 3;

        Task parentTask = Mockito.spy(new Task());

        Task detailToRemove = new Task();
        detailToRemove.setParentTaskId(parentTaskId);
        detailToRemove.setPosition(1);

        List<Task> details = new ArrayList<>();
        Task detail1 = new Task();
        detail1.setParentTaskId(parentTaskId);
        detail1.setPosition(0);
        details.add(detail1);
        Task detail3 = new Task();
        detail3.setParentTaskId(parentTaskId);
        detail3.setPosition(detail3Position);
        details.add(detail3);
        Task detail4 = new Task();
        detail4.setParentTaskId(parentTaskId);
        detail4.setPosition(detail4Position);
        details.add(detail4);

        Mockito.when(parentTask.getDetails()).thenReturn(details);

        Mockito.doReturn(parentTask).when(tasksDao).getById(parentTaskId);
        Mockito.doReturn(detailToRemove).when(tasksDao).getById(taskIdOfDetailToRemove);

        ArgumentCaptor<Task> argumentCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(argumentCaptor.capture());

        tasksService.removeTask(taskIdOfDetailToRemove);

        Mockito.verify(tasksDao, times(2)).save(anyObject());

        for (Task argument : argumentCaptor.getAllValues()) {
            if (argument == detail3) {
                Assert.assertEquals("Position of detail3 should be updated to 1", 1, argument.getPosition());
            } else if (argument == detail4) {
                Assert.assertEquals("Position of detail4 should be updated to 2", 2, argument.getPosition());
            } else {
                Assert.fail("Unexpected tasks should not be saved");
            }
        }
    }

    @Test
    public void testCompleteTask() {
        int taskId = 1;

        ArgumentCaptor<Task> savedTaskCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(savedTaskCaptor.capture());

        Task task = new Task();
        task.setProjectId(123);
        task.setTaskString("Task");
        Mockito.doReturn(task).when(tasksDao).getById(taskId);

        tasksService.completeTask(taskId);
        Mockito.verify(tasksDao).save(anyObject());

        Task savedTask = savedTaskCaptor.getValue();
        Assert.assertSame("The saved task should be the task that was set as completed", task, savedTask);
        Assert.assertTrue("The task should be set as completed", savedTask.isCompleted());
    }

    @Test
    public void testCompleteTaskThrowsForIllegalTaskId() {
        try {
            tasksService.completeTask(0);
            Assert.fail("Zero task id should throw an exception");
        } catch (IllegalArgumentException ex) {
        }

        try {
            tasksService.completeTask(-1);
            Assert.fail("Negative task id should throw an exception");
        } catch (IllegalArgumentException ex) {
        }

        try {
            tasksService.completeTask(-354);
            Assert.fail("Negative task id should throw an exception");
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test(expected = TaskDoesNotExistException.class)
    public void testCompleteTaskThrowsForNonExistingTask() {
        int taskId = 1;
        Mockito.when(tasksDao.getById(taskId)).thenReturn(null);

        tasksService.completeTask(taskId);
    }

    @Test(expected = TaskAlreadyCompletedException.class)
    public void testCompleteTaskThrowsForAlreadyCompletedTask() {
        int taskId = 1;

        Task task = new Task();
        task.setCompleted(true);
        Mockito.when(tasksDao.getById(taskId)).thenReturn(task);

        tasksService.completeTask(taskId);
    }
}
