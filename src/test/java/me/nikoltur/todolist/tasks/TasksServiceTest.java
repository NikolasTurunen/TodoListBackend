package me.nikoltur.todolist.tasks;

import java.util.ArrayList;
import java.util.List;
import me.nikoltur.todolist.projects.ProjectDoesNotExistException;
import me.nikoltur.todolist.projects.da.Project;
import me.nikoltur.todolist.projects.da.ProjectsDao;
import me.nikoltur.todolist.tasks.da.Task;
import me.nikoltur.todolist.tasks.da.TasksDao;
import org.junit.Assert;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import org.mockito.MockitoAnnotations;

/**
 *
 * @author Nikolas Turunen
 */
public class TasksServiceTest {

    private static final int PROJECT_ID = 1;
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
        Task task = createTask(PROJECT_ID);
        task.setTaskString(taskString);
        tasks.add(task);

        Project project = new Project();
        Mockito.doReturn(project).when(projectsDao).getById(PROJECT_ID);

        Mockito.doReturn(tasks).when(tasksDao).getAllOf(PROJECT_ID);

        Assert.assertEquals("Size should be 1", 1, tasksService.getTasks(1).size());
        Assert.assertEquals("Task string should match", taskString, tasksService.getTasks(1).get(0).getTaskString());
    }

    @Test(expected = ProjectDoesNotExistException.class)
    public void testGetTasksThrowsForNonExistingProject() {
        Mockito.doReturn(null).when(tasksDao).getAllOf(PROJECT_ID);

        tasksService.getTasks(PROJECT_ID);
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
        String taskString = "Hello world";

        ArgumentCaptor<Task> argumentCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(argumentCaptor.capture());

        Project project = new Project();
        Mockito.doReturn(project).when(projectsDao).getById(PROJECT_ID);

        tasksService.createTask(PROJECT_ID, taskString);

        Assert.assertEquals("Saved task should have the specified id", PROJECT_ID, (int) argumentCaptor.getValue().getProjectId());
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
        String taskString = null;

        tasksService.createTask(PROJECT_ID, taskString);
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

        Task task = createTask(PROJECT_ID);
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

        Task task = createTask(PROJECT_ID);
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

        Task task = createTask(PROJECT_ID);
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

        Task parentTask = mock(Task.class);
        Mockito.when(parentTask.getProjectId()).thenReturn(PROJECT_ID);
        Mockito.doReturn(parentTask).when(tasksDao).getById(parentTaskId);

        tasksService.createDetail(parentTaskId, detailString);

        Mockito.verify(tasksDao, times(1)).save(anyObject());
        Assert.assertEquals("Parent task id of the saved task should be the specified task id", parentTaskId, argumentCaptor.getValue().getParentTaskId());
        Assert.assertEquals("Project id of the saved task should be the project id of the parent task", (Integer) PROJECT_ID, argumentCaptor.getValue().getProjectId());
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

        Task task = createTask(PROJECT_ID);
        task.setPosition(positionOfTask);

        Task task2 = createTask(PROJECT_ID);
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

        Task task = createTask(PROJECT_ID);

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
        int projectId2 = 2;

        int taskId = 1;
        int taskId2 = 2;

        Task task = createTask(PROJECT_ID);
        Task task2 = createTask(projectId2);

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

        Task task = createTask(PROJECT_ID);
        task.setParentTaskId(parentTaskId);

        Task task2 = createTask(PROJECT_ID);
        task2.setParentTaskId(parentTaskId2);

        Mockito.doReturn(task).when(tasksDao).getById(taskId);
        Mockito.doReturn(task2).when(tasksDao).getById(taskId2);

        tasksService.swapPositionsOfTasks(taskId, taskId2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSwapPositionsOfTasksThrowsForMixedTaskDetailAndTask() {
        int taskId = 1;
        int taskId2 = 2;

        Task task = createTask(PROJECT_ID);
        task.setParentTaskId(null);
        Task task2 = createTask(PROJECT_ID);
        task2.setParentTaskId(10);

        Mockito.doReturn(task).when(tasksDao).getById(taskId);
        Mockito.doReturn(task2).when(tasksDao).getById(taskId2);

        tasksService.swapPositionsOfTasks(taskId, taskId2);
    }

    @Test
    public void testSwapPositionsOfTasksThrowsForOtherThanPreviousOrNext() {
        int taskId1 = 1;
        int taskId2 = 2;

        Task task1 = createTask(PROJECT_ID);
        task1.setPosition(1);

        Task task2 = createTask(PROJECT_ID);
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

        Project project = new Project();
        project.setName(projectName);

        Mockito.doReturn(project).when(projectsDao).getById(PROJECT_ID);

        ArgumentCaptor<Task> argumentCaptorForFirstTask = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(argumentCaptorForFirstTask.capture());

        Mockito.doReturn(new ArrayList<>()).when(tasksDao).getAllOf(PROJECT_ID);

        tasksService.createTask(PROJECT_ID, taskString);
        Assert.assertEquals("Position of the created task should be set to 0", 0, argumentCaptorForFirstTask.getValue().getPosition());

        ArgumentCaptor<Task> argumentCaptorForSecondTask = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(argumentCaptorForSecondTask.capture());

        List<Task> tasks = new ArrayList<>();
        tasks.add(createTask(PROJECT_ID));

        Mockito.doReturn(tasks).when(tasksDao).getAllOf(PROJECT_ID);

        tasksService.createTask(PROJECT_ID, taskString);
        Assert.assertEquals("Position of the created task should be set to 1", 1, argumentCaptorForSecondTask.getValue().getPosition());
    }

    @Test
    public void testCreateDetailSetsCorrectPosition() {
        int parentTaskId = 1;

        Task parentTask = mock(Task.class);
        Mockito.doReturn(parentTask).when(tasksDao).getById(parentTaskId);

        Mockito.when(parentTask.getDetails()).thenReturn(new ArrayList<>());

        ArgumentCaptor<Task> argumentCaptorForFirstDetail = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(argumentCaptorForFirstDetail.capture());

        tasksService.createDetail(parentTaskId, "Detail");

        Assert.assertEquals("Position of the created detail should be set to 0", 0, argumentCaptorForFirstDetail.getValue().getPosition());

        List<Task> details = new ArrayList<>();
        details.add(createTask(PROJECT_ID));
        Mockito.when(parentTask.getDetails()).thenReturn(details);

        ArgumentCaptor<Task> argumentCaptorForSecondDetail = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(argumentCaptorForSecondDetail.capture());

        tasksService.createDetail(parentTaskId, "Detail2");

        Assert.assertEquals("Position of the created detail should be set to 1", 1, argumentCaptorForSecondDetail.getValue().getPosition());
    }

    @Test
    public void testRemoveTaskDecrementsPositionsOfTasksWithHigherPosition() {
        int taskId = 1;

        Task taskToRemove = createTask(PROJECT_ID);
        taskToRemove.setPosition(1);

        List<Task> tasks = new ArrayList<>();
        Task task1 = createTask(PROJECT_ID);
        task1.setPosition(0);
        tasks.add(task1);
        Task task3 = createTask(PROJECT_ID);
        task3.setPosition(2);
        tasks.add(task3);
        Task task4 = createTask(PROJECT_ID);
        task4.setPosition(3);
        tasks.add(task4);

        Mockito.doReturn(tasks).when(tasksDao).getAllOf(PROJECT_ID);

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

        Task parentTask = mock(Task.class);

        Task detailToRemove = createTask(PROJECT_ID);
        detailToRemove.setParentTaskId(parentTaskId);
        detailToRemove.setPosition(1);

        List<Task> details = new ArrayList<>();
        Task detail1 = createTask(PROJECT_ID);
        detail1.setParentTaskId(parentTaskId);
        detail1.setPosition(0);
        details.add(detail1);
        Task detail3 = createTask(PROJECT_ID);
        detail3.setParentTaskId(parentTaskId);
        detail3.setPosition(detail3Position);
        details.add(detail3);
        Task detail4 = createTask(PROJECT_ID);
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

        Task task = createTask(PROJECT_ID);
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

        Task task = createTask(PROJECT_ID);
        task.setCompleted(true);
        Mockito.when(tasksDao.getById(taskId)).thenReturn(task);

        tasksService.completeTask(taskId);
    }

    @Test
    public void testuncompleteTask() {
        int taskId = 1;

        ArgumentCaptor<Task> savedTaskCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(savedTaskCaptor.capture());

        Task task = createTask(PROJECT_ID);
        task.setCompleted(true);
        task.setTaskString("Task");
        Mockito.doReturn(task).when(tasksDao).getById(taskId);

        tasksService.uncompleteTask(taskId);
        Mockito.verify(tasksDao).save(anyObject());

        Task savedTask = savedTaskCaptor.getValue();
        Assert.assertSame("The saved task should be the task that was set as completed", task, savedTask);
        Assert.assertFalse("The task should be set as not completed", savedTask.isCompleted());
    }

    @Test
    public void testUncompleteTaskThrowsForIllegalTaskId() {
        try {
            tasksService.uncompleteTask(0);
            Assert.fail("Zero task id should throw an exception");
        } catch (IllegalArgumentException ex) {
        }

        try {
            tasksService.uncompleteTask(-1);
            Assert.fail("Negative task id should throw an exception");
        } catch (IllegalArgumentException ex) {
        }

        try {
            tasksService.uncompleteTask(-354);
            Assert.fail("Negative task id should throw an exception");
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test(expected = TaskDoesNotExistException.class)
    public void testUncompleteTaskThrowsForNonExistingTask() {
        int taskId = 1;
        Mockito.when(tasksDao.getById(taskId)).thenReturn(null);

        tasksService.uncompleteTask(taskId);
    }

    @Test(expected = TaskNotCompletedException.class)
    public void testUncompleteTaskThrowsForNotCompletedTask() {
        int taskId = 1;

        Task task = createTask(PROJECT_ID);
        task.setCompleted(false);
        Mockito.when(tasksDao.getById(taskId)).thenReturn(task);

        tasksService.uncompleteTask(taskId);
    }

    @Test
    public void testMoveTask() {
        int taskId = 1;
        int newParentTaskId = 2;

        int currentParentTaskId = 3;

        ArgumentCaptor<Task> savedTaskCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(savedTaskCaptor.capture());

        Task task = spy(createTask(PROJECT_ID));
        task.setParentTaskId(currentParentTaskId);
        Mockito.when(tasksDao.getById(taskId)).thenReturn(task);

        Mockito.when(task.getDetails()).thenReturn(new ArrayList<>());

        Task newParentTask = spy(createTask(PROJECT_ID));
        Mockito.when(tasksDao.getById(newParentTaskId)).thenReturn(newParentTask);

        Mockito.when(newParentTask.getDetails()).thenReturn(new ArrayList<>());

        Task currentParentTask = mock(Task.class);
        Mockito.when(tasksDao.getById(currentParentTaskId)).thenReturn(currentParentTask);

        Mockito.when(currentParentTask.getDetails()).thenReturn(new ArrayList<>());

        tasksService.moveTask(taskId, newParentTaskId, null);

        Task savedTask = savedTaskCaptor.getValue();
        Assert.assertSame("The moved task should be saved", task, savedTask);
        Assert.assertEquals("Parent task id of the saved task should be updated to the new parent task id", newParentTaskId, (int) savedTask.getParentTaskId());
    }

    @Test
    public void testMoveTaskUpdatesProjectIdWithNullNewParentTaskId() {
        int taskId = 1;
        Integer newParentTaskId = null;
        int newProjectId = 5;

        int currentParentTaskId = 3;

        ArgumentCaptor<Task> savedTaskCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(savedTaskCaptor.capture());

        Task task = spy(createTask(PROJECT_ID));
        task.setParentTaskId(currentParentTaskId);
        Mockito.when(tasksDao.getById(taskId)).thenReturn(task);

        Mockito.when(task.getDetails()).thenReturn(new ArrayList<>());

        Task currentParentTask = mock(Task.class);
        Mockito.when(tasksDao.getById(currentParentTaskId)).thenReturn(currentParentTask);

        Mockito.when(currentParentTask.getDetails()).thenReturn(new ArrayList<>());

        Project newProject = mock(Project.class);
        Mockito.when(projectsDao.getById(newProjectId)).thenReturn(newProject);

        tasksService.moveTask(taskId, newParentTaskId, newProjectId);

        Task savedTask = savedTaskCaptor.getValue();
        Assert.assertSame("The moved task should be saved", task, savedTask);
        Assert.assertEquals("Project id of the specified task should be changed", newProjectId, (int) savedTask.getProjectId());
    }

    @Test
    public void testMoveTaskUpdatesProjectIdOfTaskWithNullParentTaskId() {
        int taskId = 1;
        Integer newParentTaskId = null;
        int newProjectId = 5;

        Integer currentParentTaskId = null;

        ArgumentCaptor<Task> savedTaskCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(savedTaskCaptor.capture());

        Task task = spy(createTask(PROJECT_ID));
        task.setParentTaskId(currentParentTaskId);
        Mockito.when(tasksDao.getById(taskId)).thenReturn(task);

        Mockito.when(task.getDetails()).thenReturn(new ArrayList<>());

        Project newProject = mock(Project.class);
        Mockito.when(projectsDao.getById(newProjectId)).thenReturn(newProject);

        tasksService.moveTask(taskId, newParentTaskId, newProjectId);

        Task savedTask = savedTaskCaptor.getValue();
        Assert.assertSame("The moved task should be saved", task, savedTask);
        Assert.assertEquals("Project id of the specified task should be changed", newProjectId, (int) savedTask.getProjectId());
    }

    @Test
    public void testMoveTaskDoesNotUpdateProjectIdWithNonNullNewParentTaskId() {
        int taskId = 1;
        int newParentTaskId = 2;
        int newProjectId = 5;

        int currentParentTaskId = 3;

        ArgumentCaptor<Task> savedTaskCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(savedTaskCaptor.capture());

        Task task = spy(createTask(PROJECT_ID));
        task.setParentTaskId(currentParentTaskId);
        Mockito.when(tasksDao.getById(taskId)).thenReturn(task);

        Mockito.when(task.getDetails()).thenReturn(new ArrayList<>());

        Task newParentTask = spy(createTask(PROJECT_ID));
        Mockito.when(tasksDao.getById(newParentTaskId)).thenReturn(newParentTask);

        Mockito.when(newParentTask.getDetails()).thenReturn(new ArrayList<>());

        Task currentParentTask = mock(Task.class);
        Mockito.when(tasksDao.getById(currentParentTaskId)).thenReturn(currentParentTask);

        Mockito.when(currentParentTask.getDetails()).thenReturn(new ArrayList<>());

        Project newProject = mock(Project.class);
        Mockito.when(projectsDao.getById(newProjectId)).thenReturn(newProject);

        tasksService.moveTask(taskId, newParentTaskId, newProjectId);

        Task savedTask = savedTaskCaptor.getValue();
        Assert.assertEquals("Project id of the specified task should be unchanged", PROJECT_ID, (int) savedTask.getProjectId());
    }

    @Test
    public void testMoveTaskWithNewProjectIdUpdatesPositionsOfTasksWithHigherPositionAtOldLocation() {
        int taskId = 1;
        Integer newParentTaskId = null;

        int newProjectId = 5;

        ArgumentCaptor<Task> savedTaskCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(savedTaskCaptor.capture());

        Task task = spy(createTask(PROJECT_ID));
        task.setPosition(1);
        Mockito.when(tasksDao.getById(taskId)).thenReturn(task);

        Mockito.when(task.getDetails()).thenReturn(new ArrayList<>());

        List<Task> tasksOfProject = new ArrayList<>();
        tasksOfProject.add(createTask(PROJECT_ID));
        tasksOfProject.add(createTask(PROJECT_ID));
        tasksOfProject.add(createTask(PROJECT_ID));
        Mockito.when(tasksDao.getAllOf(PROJECT_ID)).thenReturn(tasksOfProject);

        List<Task> tasksOfCurrentProject = new ArrayList<>();
        Task task1 = createTask(PROJECT_ID);
        task1.setPosition(0);
        tasksOfCurrentProject.add(task1);
        tasksOfCurrentProject.add(task);
        Task task2 = createTask(PROJECT_ID);
        task2.setPosition(2);
        tasksOfCurrentProject.add(task2);
        Task task3 = createTask(PROJECT_ID);
        task3.setPosition(3);
        tasksOfCurrentProject.add(task3);
        Mockito.when(tasksDao.getAllOf(PROJECT_ID)).thenReturn(tasksOfCurrentProject);

        Project newProject = mock(Project.class);
        Mockito.when(projectsDao.getById(newProjectId)).thenReturn(newProject);

        tasksService.moveTask(taskId, newParentTaskId, newProjectId);

        Assert.assertFalse("Task 1 should not be saved", savedTaskCaptor.getAllValues().contains(task1));
        Assert.assertTrue("Task 2 should be saved", savedTaskCaptor.getAllValues().contains(task2));
        Assert.assertTrue("Task 3 should be saved", savedTaskCaptor.getAllValues().contains(task3));

        for (Task savedTask : savedTaskCaptor.getAllValues()) {
            if (savedTask == task2) {
                Assert.assertEquals("Position of task 2 should be updated to 1", 1, savedTask.getPosition());
            } else if (savedTask == task3) {
                Assert.assertEquals("Position of task 3 should be updated to 2", 2, savedTask.getPosition());
            }
        }
    }

    @Test
    public void testMoveTaskWithNewProjectIdUpdatesPositionToLast() {
        int taskId = 1;
        Integer newParentTaskId = null;

        int currentParentTaskId = 3;

        int newProjectId = 5;

        ArgumentCaptor<Task> savedTaskCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(savedTaskCaptor.capture());

        Task task = spy(createTask(PROJECT_ID));
        task.setParentTaskId(currentParentTaskId);
        Mockito.when(tasksDao.getById(taskId)).thenReturn(task);

        Mockito.when(task.getDetails()).thenReturn(new ArrayList<>());

        List<Task> tasksOfProject = new ArrayList<>();
        tasksOfProject.add(createTask(PROJECT_ID));
        tasksOfProject.add(createTask(PROJECT_ID));
        tasksOfProject.add(createTask(PROJECT_ID));
        Mockito.when(tasksDao.getAllOf(newProjectId)).thenReturn(tasksOfProject);

        Task currentParentTask = mock(Task.class);
        Mockito.when(tasksDao.getById(currentParentTaskId)).thenReturn(currentParentTask);

        Mockito.when(currentParentTask.getDetails()).thenReturn(new ArrayList<>());

        Project newProject = mock(Project.class);
        Mockito.when(projectsDao.getById(newProjectId)).thenReturn(newProject);

        tasksService.moveTask(taskId, newParentTaskId, newProjectId);

        int expectedPosition = tasksOfProject.size();

        Task savedTask = savedTaskCaptor.getValue();
        Assert.assertSame("The moved task should be saved", task, savedTask);
        Assert.assertEquals("Position of the saved task should be updated to be the last", expectedPosition, savedTask.getPosition());
    }

    @Test
    public void testMoveTaskWithNewProjectIdUpdatesProjectIdsOfDetails() {
        int taskId = 1;
        Integer newParentTaskId = null;

        int currentParentTaskId = 3;

        int newProjectId = 5;

        ArgumentCaptor<Task> savedTaskCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(savedTaskCaptor.capture());

        Task task = spy(createTask(PROJECT_ID));
        task.setParentTaskId(currentParentTaskId);
        Mockito.when(tasksDao.getById(taskId)).thenReturn(task);

        List<Task> detailsOfTask = new ArrayList<>();
        Task detail1 = spy(createTask(PROJECT_ID));
        Mockito.when(detail1.getId()).thenReturn(1001);
        Mockito.when(detail1.getDetails()).thenReturn(new ArrayList<>());
        detailsOfTask.add(detail1);

        Task detail2 = spy(createTask(PROJECT_ID));
        Mockito.when(detail2.getId()).thenReturn(1002);
        List<Task> nestedDetailsOfDetail2 = new ArrayList<>();

        Task nestedDetail1 = spy(createTask(PROJECT_ID));
        Mockito.when(nestedDetail1.getId()).thenReturn(1003);
        Mockito.when(nestedDetail1.getDetails()).thenReturn(new ArrayList<>());
        nestedDetailsOfDetail2.add(nestedDetail1);

        Task nestedDetail2 = spy(createTask(PROJECT_ID));
        Mockito.when(nestedDetail2.getId()).thenReturn(1004);
        Mockito.when(nestedDetail2.getDetails()).thenReturn(new ArrayList<>());
        nestedDetailsOfDetail2.add(nestedDetail2);
        Mockito.when(detail2.getDetails()).thenReturn(nestedDetailsOfDetail2);
        detailsOfTask.add(detail2);

        Mockito.when(task.getDetails()).thenReturn(detailsOfTask);

        List<Task> tasksOfProject = new ArrayList<>();
        tasksOfProject.add(createTask(PROJECT_ID));
        tasksOfProject.add(createTask(PROJECT_ID));
        tasksOfProject.add(createTask(PROJECT_ID));
        Mockito.when(tasksDao.getAllOf(newProjectId)).thenReturn(tasksOfProject);

        Task currentParentTask = mock(Task.class);
        Mockito.when(tasksDao.getById(currentParentTaskId)).thenReturn(currentParentTask);

        Mockito.when(currentParentTask.getDetails()).thenReturn(new ArrayList<>());

        Project newProject = mock(Project.class);
        Mockito.when(projectsDao.getById(newProjectId)).thenReturn(newProject);

        tasksService.moveTask(taskId, newParentTaskId, newProjectId);

        List<Task> savedTasks = savedTaskCaptor.getAllValues();
        Assert.assertTrue("Detail 1 should be saved", savedTasks.contains(detail1));
        Assert.assertEquals("Project id of detail 1 should be updated", newProjectId, (int) detail1.getProjectId());

        Assert.assertTrue("Detail 2 should be saved", savedTasks.contains(detail2));
        Assert.assertEquals("Project id of detail 2 should be updated", newProjectId, (int) detail2.getProjectId());

        Assert.assertTrue("Nested detail 1 should be saved", savedTasks.contains(nestedDetail1));
        Assert.assertEquals("Project id of nested detail 1 should be updated", newProjectId, (int) nestedDetail1.getProjectId());

        Assert.assertTrue("Nested detail 2 should be saved", savedTasks.contains(nestedDetail2));
        Assert.assertEquals("Project id of nested detail 2 should be updated", newProjectId, (int) nestedDetail2.getProjectId());
    }

    @Test(expected = ProjectDoesNotExistException.class)
    public void testMoveTaskThrowsForNonExistantNonNullNewProjectIdWithNullNewParentTaskId() {
        int taskId = 1;
        Integer newParentTaskId = null;
        int newProjectId = 5;

        int currentParentTaskId = 3;

        Task task = spy(createTask(PROJECT_ID));
        task.setParentTaskId(currentParentTaskId);
        Mockito.when(tasksDao.getById(taskId)).thenReturn(task);

        Task currentParentTask = mock(Task.class);
        Mockito.when(tasksDao.getById(currentParentTaskId)).thenReturn(currentParentTask);

        Mockito.when(currentParentTask.getDetails()).thenReturn(new ArrayList<>());

        Mockito.when(projectsDao.getById(newProjectId)).thenReturn(null);

        tasksService.moveTask(taskId, newParentTaskId, newProjectId);
    }

    @Test
    public void testMoveTaskWithNullNewParentTaskId() {
        int taskId = 1;
        Integer newParentTaskId = null;

        int currentParentTaskId = 3;

        ArgumentCaptor<Task> savedTaskCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(savedTaskCaptor.capture());

        Task task = spy(createTask(PROJECT_ID));
        task.setParentTaskId(currentParentTaskId);
        Mockito.when(tasksDao.getById(taskId)).thenReturn(task);

        Mockito.when(task.getDetails()).thenReturn(new ArrayList<>());

        Task currentParentTask = mock(Task.class);
        Mockito.when(tasksDao.getById(currentParentTaskId)).thenReturn(currentParentTask);

        Mockito.when(currentParentTask.getDetails()).thenReturn(new ArrayList<>());

        tasksService.moveTask(taskId, newParentTaskId, null);

        Task savedTask = savedTaskCaptor.getValue();
        Assert.assertSame("The moved task should be saved", task, savedTask);
        Assert.assertNull("Parent task id of the saved task should be updated to the new parent task id", savedTask.getParentTaskId());
    }

    @Test
    public void testMoveTaskWithNullNewParentTaskIdUpdatesPositionToLast() {
        int taskId = 1;
        Integer newParentTaskId = null;

        int currentParentTaskId = 3;

        ArgumentCaptor<Task> savedTaskCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(savedTaskCaptor.capture());

        Task task = spy(createTask(PROJECT_ID));
        task.setParentTaskId(currentParentTaskId);
        Mockito.when(tasksDao.getById(taskId)).thenReturn(task);

        Mockito.when(task.getDetails()).thenReturn(new ArrayList<>());

        List<Task> tasksOfProject = new ArrayList<>();
        tasksOfProject.add(createTask(PROJECT_ID));
        tasksOfProject.add(createTask(PROJECT_ID));
        tasksOfProject.add(createTask(PROJECT_ID));
        Mockito.when(tasksDao.getAllOf(PROJECT_ID)).thenReturn(tasksOfProject);

        Task currentParentTask = mock(Task.class);
        Mockito.when(tasksDao.getById(currentParentTaskId)).thenReturn(currentParentTask);

        Mockito.when(currentParentTask.getDetails()).thenReturn(new ArrayList<>());

        tasksService.moveTask(taskId, newParentTaskId, null);

        int expectedPosition = tasksOfProject.size();

        Task savedTask = savedTaskCaptor.getValue();
        Assert.assertSame("The moved task should be saved", task, savedTask);
        Assert.assertEquals("Position of the saved task should be updated to be the last", expectedPosition, savedTask.getPosition());
    }

    @Test
    public void testMoveTaskWithNullNewParentTaskIdUpdatesPositionsOfTasksWithHigherPositionAtOldLocation() {
        int taskId = 1;
        Integer newParentTaskId = null;

        ArgumentCaptor<Task> savedTaskCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(savedTaskCaptor.capture());

        int currentParentTaskId = 3;

        Task task = spy(createTask(PROJECT_ID));
        task.setPosition(1);
        task.setParentTaskId(currentParentTaskId);
        Mockito.when(tasksDao.getById(taskId)).thenReturn(task);

        Mockito.when(task.getDetails()).thenReturn(new ArrayList<>());

        List<Task> tasksOfProject = new ArrayList<>();
        tasksOfProject.add(createTask(PROJECT_ID));
        tasksOfProject.add(createTask(PROJECT_ID));
        tasksOfProject.add(createTask(PROJECT_ID));
        Mockito.when(tasksDao.getAllOf(PROJECT_ID)).thenReturn(tasksOfProject);

        Task currentParentTask = mock(Task.class);
        Mockito.when(tasksDao.getById(currentParentTaskId)).thenReturn(currentParentTask);

        List<Task> detailsOfParentTaskOfSource = new ArrayList<>();
        Task task1 = createTask(PROJECT_ID);
        task1.setPosition(0);
        detailsOfParentTaskOfSource.add(task1);
        detailsOfParentTaskOfSource.add(task);
        Task task2 = createTask(PROJECT_ID);
        task2.setPosition(2);
        detailsOfParentTaskOfSource.add(task2);
        Task task3 = createTask(PROJECT_ID);
        task3.setPosition(3);
        detailsOfParentTaskOfSource.add(task3);
        Mockito.when(currentParentTask.getDetails()).thenReturn(detailsOfParentTaskOfSource);

        tasksService.moveTask(taskId, newParentTaskId, null);

        Assert.assertFalse("Task 1 should not be saved", savedTaskCaptor.getAllValues().contains(task1));
        Assert.assertTrue("Task 2 should be saved", savedTaskCaptor.getAllValues().contains(task2));
        Assert.assertTrue("Task 3 should be saved", savedTaskCaptor.getAllValues().contains(task3));

        for (Task savedTask : savedTaskCaptor.getAllValues()) {
            if (savedTask == task2) {
                Assert.assertEquals("Position of task 2 should be updated to 1", 1, savedTask.getPosition());
            } else if (savedTask == task3) {
                Assert.assertEquals("Position of task 3 should be updated to 2", 2, savedTask.getPosition());
            }
        }
    }

    @Test(expected = TaskDoesNotHaveParentException.class)
    public void testMoveTaskWithNullNewParentTaskIdThrowsIfParentTaskIdIsAlreadyNull() {
        int taskId = 1;
        Integer newParentTaskId = null;

        Integer currentParentTaskId = null;

        Task task = spy(createTask(PROJECT_ID));
        task.setParentTaskId(currentParentTaskId);
        Mockito.when(tasksDao.getById(taskId)).thenReturn(task);

        tasksService.moveTask(taskId, newParentTaskId, null);
    }

    @Test
    public void testMoveTaskToDifferentProjectUpdatesProjectId() {
        int projectId2 = 2;

        int taskId = 1;
        int newParentTaskId = 2;

        int currentParentTaskId = 3;

        ArgumentCaptor<Task> savedTaskCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(savedTaskCaptor.capture());

        Task task = spy(createTask(PROJECT_ID));
        task.setParentTaskId(currentParentTaskId);
        Mockito.when(tasksDao.getById(taskId)).thenReturn(task);

        Mockito.when(task.getDetails()).thenReturn(new ArrayList<>());

        Task newParentTask = spy(createTask(projectId2));
        Mockito.when(tasksDao.getById(newParentTaskId)).thenReturn(newParentTask);

        Mockito.when(newParentTask.getDetails()).thenReturn(new ArrayList<>());

        Task currentParentTask = mock(Task.class);
        Mockito.when(tasksDao.getById(currentParentTaskId)).thenReturn(currentParentTask);

        Mockito.when(currentParentTask.getDetails()).thenReturn(new ArrayList<>());

        tasksService.moveTask(taskId, newParentTaskId, null);

        Task savedTask = savedTaskCaptor.getValue();
        Assert.assertSame("The moved task should be saved", task, savedTask);
        Assert.assertEquals("Project id of the saved task should be updated to the project id of the new parent task", projectId2, (int) savedTask.getProjectId());
    }

    @Test
    public void testMoveTaskUpdatesPositionToLast() {
        int taskId = 1;
        int newParentTaskId = 2;

        int currentParentTaskId = 3;

        ArgumentCaptor<Task> savedTaskCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(savedTaskCaptor.capture());

        Task task = spy(createTask(PROJECT_ID));
        task.setParentTaskId(currentParentTaskId);
        Mockito.when(tasksDao.getById(taskId)).thenReturn(task);

        Mockito.when(task.getDetails()).thenReturn(new ArrayList<>());

        Task newParentTask = spy(createTask(PROJECT_ID));
        Mockito.when(tasksDao.getById(newParentTaskId)).thenReturn(newParentTask);

        List<Task> detailsOfNewParentTask = new ArrayList<>();
        detailsOfNewParentTask.add(createTask(PROJECT_ID));
        detailsOfNewParentTask.add(createTask(PROJECT_ID));
        detailsOfNewParentTask.add(createTask(PROJECT_ID));
        Mockito.when(newParentTask.getDetails()).thenReturn(detailsOfNewParentTask);

        Task currentParentTask = mock(Task.class);
        Mockito.when(tasksDao.getById(currentParentTaskId)).thenReturn(currentParentTask);

        Mockito.when(currentParentTask.getDetails()).thenReturn(new ArrayList<>());

        tasksService.moveTask(taskId, newParentTaskId, null);

        int expectedPosition = detailsOfNewParentTask.size();

        Task savedTask = savedTaskCaptor.getValue();
        Assert.assertSame("The moved task should be saved", task, savedTask);
        Assert.assertEquals("Position of the saved task should be updated to be the last", expectedPosition, savedTask.getPosition());
    }

    @Test
    public void testMoveTaskUpdatesPositionsOfTasksWithHigherPositionAtOldLocation() {
        int taskId = 1;
        int newParentTaskId = 2;

        ArgumentCaptor<Task> savedTaskCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(savedTaskCaptor.capture());

        int currentParentTaskId = 3;

        Task task = spy(createTask(PROJECT_ID));
        task.setPosition(1);
        task.setParentTaskId(currentParentTaskId);
        Mockito.when(tasksDao.getById(taskId)).thenReturn(task);

        Mockito.when(task.getDetails()).thenReturn(new ArrayList<>());

        Task newParentTask = spy(createTask(PROJECT_ID));
        Mockito.when(tasksDao.getById(newParentTaskId)).thenReturn(newParentTask);

        Mockito.when(newParentTask.getDetails()).thenReturn(new ArrayList<>());

        Task currentParentTask = mock(Task.class);
        Mockito.when(tasksDao.getById(currentParentTaskId)).thenReturn(currentParentTask);

        List<Task> detailsOfParentTaskOfSource = new ArrayList<>();
        Task task1 = createTask(PROJECT_ID);
        task1.setPosition(0);
        detailsOfParentTaskOfSource.add(task1);
        detailsOfParentTaskOfSource.add(task);
        Task task2 = createTask(PROJECT_ID);
        task2.setPosition(2);
        detailsOfParentTaskOfSource.add(task2);
        Task task3 = createTask(PROJECT_ID);
        task3.setPosition(3);
        detailsOfParentTaskOfSource.add(task3);
        Mockito.when(currentParentTask.getDetails()).thenReturn(detailsOfParentTaskOfSource);

        tasksService.moveTask(taskId, newParentTaskId, null);

        Assert.assertFalse("Task 1 should not be saved", savedTaskCaptor.getAllValues().contains(task1));
        Assert.assertTrue("Task 2 should be saved", savedTaskCaptor.getAllValues().contains(task2));
        Assert.assertTrue("Task 3 should be saved", savedTaskCaptor.getAllValues().contains(task3));

        for (Task savedTask : savedTaskCaptor.getAllValues()) {
            if (savedTask == task2) {
                Assert.assertEquals("Position of task 2 should be updated to 1", 1, savedTask.getPosition());
            } else if (savedTask == task3) {
                Assert.assertEquals("Position of task 3 should be updated to 2", 2, savedTask.getPosition());
            }
        }
    }

    @Test
    public void testMoveTaskWithNoParentTaskUpdatesPositionsOfTasksWithHigherPositionAtOldLocation() {
        int taskId = 1;
        int newParentTaskId = 2;

        ArgumentCaptor<Task> savedTaskCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(savedTaskCaptor.capture());

        Task task = spy(createTask(PROJECT_ID));
        task.setPosition(1);
        Mockito.when(tasksDao.getById(taskId)).thenReturn(task);

        Mockito.when(task.getDetails()).thenReturn(new ArrayList<>());

        Task newParentTask = spy(createTask(PROJECT_ID));
        Mockito.when(tasksDao.getById(newParentTaskId)).thenReturn(newParentTask);

        Mockito.when(newParentTask.getDetails()).thenReturn(new ArrayList<>());

        List<Task> tasksOfCurrentProject = new ArrayList<>();
        Task task1 = createTask(PROJECT_ID);
        task1.setPosition(0);
        tasksOfCurrentProject.add(task1);
        tasksOfCurrentProject.add(task);
        Task task2 = createTask(PROJECT_ID);
        task2.setPosition(2);
        tasksOfCurrentProject.add(task2);
        Task task3 = createTask(PROJECT_ID);
        task3.setPosition(3);
        tasksOfCurrentProject.add(task3);
        Mockito.when(tasksDao.getAllOf(PROJECT_ID)).thenReturn(tasksOfCurrentProject);

        tasksService.moveTask(taskId, newParentTaskId, null);

        Assert.assertFalse("Task 1 should not be saved", savedTaskCaptor.getAllValues().contains(task1));
        Assert.assertTrue("Task 2 should be saved", savedTaskCaptor.getAllValues().contains(task2));
        Assert.assertTrue("Task 3 should be saved", savedTaskCaptor.getAllValues().contains(task3));

        for (Task savedTask : savedTaskCaptor.getAllValues()) {
            if (savedTask == task2) {
                Assert.assertEquals("Position of task 2 should be updated to 1", 1, savedTask.getPosition());
            } else if (savedTask == task3) {
                Assert.assertEquals("Position of task 3 should be updated to 2", 2, savedTask.getPosition());
            }
        }
    }

    @Test
    public void testMoveTaskWorksIfCurrentParentTaskIsNull() {
        int taskId = 1;
        int newParentTaskId = 2;

        ArgumentCaptor<Task> savedTaskCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(savedTaskCaptor.capture());

        Integer currentParentTaskId = null;

        Task task = spy(createTask(PROJECT_ID));
        task.setParentTaskId(currentParentTaskId);
        Mockito.when(tasksDao.getById(taskId)).thenReturn(task);

        Mockito.when(task.getDetails()).thenReturn(new ArrayList<>());

        Task newParentTask = spy(createTask(PROJECT_ID));
        Mockito.when(tasksDao.getById(newParentTaskId)).thenReturn(newParentTask);

        Mockito.when(newParentTask.getDetails()).thenReturn(new ArrayList<>());

        tasksService.moveTask(taskId, newParentTaskId, null);

        Task savedTask = savedTaskCaptor.getValue();
        Assert.assertSame("The moved task should be saved", task, savedTask);
        Assert.assertEquals("Parent task id of the saved task should be updated to the new parent task id", newParentTaskId, (int) savedTask.getParentTaskId());
    }

    @Test
    public void testMoveTaskWithNewParentTaskInDifferentProjectUpdatesProjectIdsOfDetails() {
        int taskId = 1;
        Integer newParentTaskId = 2;

        int currentParentTaskId = 3;

        ArgumentCaptor<Task> savedTaskCaptor = ArgumentCaptor.forClass(Task.class);
        Mockito.doNothing().when(tasksDao).save(savedTaskCaptor.capture());

        Task task = spy(createTask(PROJECT_ID));
        task.setParentTaskId(currentParentTaskId);
        Mockito.when(tasksDao.getById(taskId)).thenReturn(task);

        List<Task> detailsOfTask = new ArrayList<>();
        Task detail1 = spy(createTask(PROJECT_ID));
        Mockito.when(detail1.getId()).thenReturn(1001);
        Mockito.when(detail1.getDetails()).thenReturn(new ArrayList<>());
        detailsOfTask.add(detail1);

        Task detail2 = spy(createTask(PROJECT_ID));
        Mockito.when(detail2.getId()).thenReturn(1002);
        List<Task> nestedDetailsOfDetail2 = new ArrayList<>();

        Task nestedDetail1 = spy(createTask(PROJECT_ID));
        Mockito.when(nestedDetail1.getId()).thenReturn(1003);
        Mockito.when(nestedDetail1.getDetails()).thenReturn(new ArrayList<>());
        nestedDetailsOfDetail2.add(nestedDetail1);

        Task nestedDetail2 = spy(createTask(PROJECT_ID));
        Mockito.when(nestedDetail2.getId()).thenReturn(1004);
        Mockito.when(nestedDetail2.getDetails()).thenReturn(new ArrayList<>());
        nestedDetailsOfDetail2.add(nestedDetail2);
        Mockito.when(detail2.getDetails()).thenReturn(nestedDetailsOfDetail2);
        detailsOfTask.add(detail2);

        Mockito.when(task.getDetails()).thenReturn(detailsOfTask);

        Task currentParentTask = mock(Task.class);
        Mockito.when(tasksDao.getById(currentParentTaskId)).thenReturn(currentParentTask);

        Mockito.when(currentParentTask.getDetails()).thenReturn(new ArrayList<>());

        int projectIdOfNewParentTask = 5;

        Task newParentTask = spy(createTask(PROJECT_ID));
        Mockito.when(newParentTask.getProjectId()).thenReturn(projectIdOfNewParentTask);
        Mockito.when(tasksDao.getById(newParentTaskId)).thenReturn(newParentTask);

        Mockito.when(newParentTask.getDetails()).thenReturn(new ArrayList<>());

        tasksService.moveTask(taskId, newParentTaskId, null);

        List<Task> savedTasks = savedTaskCaptor.getAllValues();
        Assert.assertTrue("Detail 1 should be saved", savedTasks.contains(detail1));
        Assert.assertEquals("Project id of detail 1 should be updated", projectIdOfNewParentTask, (int) detail1.getProjectId());

        Assert.assertTrue("Detail 2 should be saved", savedTasks.contains(detail2));
        Assert.assertEquals("Project id of detail 2 should be updated", projectIdOfNewParentTask, (int) detail2.getProjectId());

        Assert.assertTrue("Nested detail 1 should be saved", savedTasks.contains(nestedDetail1));
        Assert.assertEquals("Project id of nested detail 1 should be updated", projectIdOfNewParentTask, (int) nestedDetail1.getProjectId());

        Assert.assertTrue("Nested detail 2 should be saved", savedTasks.contains(nestedDetail2));
        Assert.assertEquals("Project id of nested detail 2 should be updated", projectIdOfNewParentTask, (int) nestedDetail2.getProjectId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoveTaskThrowsIfTheNewParentTaskIsDetailOfTaskLowerInHierarchy() {
        int taskId = 1;
        int newParentTaskId = 2;

        Task task = spy(createTask(PROJECT_ID));
        Mockito.when(tasksDao.getById(taskId)).thenReturn(task);

        Task detailOfTask = spy(createTask(PROJECT_ID));

        List<Task> detailsOfTask = new ArrayList<>();
        detailsOfTask.add(detailOfTask);

        Mockito.when(task.getDetails()).thenReturn(detailsOfTask);

        Task newParentTask = spy(createTask(PROJECT_ID));
        Mockito.when(tasksDao.getById(newParentTaskId)).thenReturn(newParentTask);

        Mockito.when(newParentTask.getDetails()).thenReturn(new ArrayList<>());

        List<Task> detailsOfDetail = new ArrayList<>();
        detailsOfDetail.add(newParentTask);

        Mockito.when(detailOfTask.getDetails()).thenReturn(detailsOfDetail);

        tasksService.moveTask(taskId, newParentTaskId, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoveTaskThrowsWhenTaskIdAndNewParentTaskIdAreEqual() {
        int taskId = 1;

        Task task = createTask(PROJECT_ID);
        Mockito.when(tasksDao.getById(taskId)).thenReturn(task);

        tasksService.moveTask(taskId, taskId, null);
    }

    @Test(expected = TaskDoesNotExistException.class)
    public void testMoveTaskThrowsWhenTaskDoesNotExist() {
        int taskId = 1;
        int newParentTaskId = 2;

        Mockito.when(tasksDao.getById(taskId)).thenReturn(null);

        Task newParentTask = createTask(PROJECT_ID);
        Mockito.when(tasksDao.getById(newParentTaskId)).thenReturn(newParentTask);

        tasksService.moveTask(taskId, newParentTaskId, null);
    }

    @Test(expected = TaskDoesNotExistException.class)
    public void testMoveTaskThrowsWhenNewParentTaskDoesNotExist() {
        int taskId = 1;
        int newParentTaskId = 2;

        Task task = createTask(PROJECT_ID);
        Mockito.when(tasksDao.getById(taskId)).thenReturn(task);

        Mockito.when(tasksDao.getById(newParentTaskId)).thenReturn(null);

        tasksService.moveTask(taskId, newParentTaskId, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMoveTaskThrowsWhenTaskIsAlreadyDetailOfNewParentTask() {
        int taskId = 1;
        int newParentTaskId = 2;

        int currentParentTaskId = newParentTaskId;

        Task task = spy(createTask(PROJECT_ID));
        task.setParentTaskId(currentParentTaskId);
        Mockito.when(tasksDao.getById(taskId)).thenReturn(task);

        Mockito.when(task.getDetails()).thenReturn(new ArrayList<>());

        Task newParentTask = spy(createTask(PROJECT_ID));
        Mockito.when(tasksDao.getById(newParentTaskId)).thenReturn(newParentTask);

        Mockito.when(newParentTask.getDetails()).thenReturn(new ArrayList<>());

        tasksService.moveTask(taskId, newParentTaskId, null);
    }

    @Test
    public void testMoveTaskThrowsForIllegalTaskId() {
        int validTaskId = 1;

        Task task = createTask(PROJECT_ID);
        Mockito.when(tasksDao.getById(validTaskId)).thenReturn(task);

        try {
            tasksService.moveTask(-1, validTaskId, null);
            fail("Negative task id should throw an exception");
        } catch (IllegalArgumentException ex) {
        }

        try {
            tasksService.moveTask(validTaskId, -1, null);
            fail("Negative new parent task id should throw an exception");
        } catch (IllegalArgumentException ex) {
        }

        try {
            tasksService.moveTask(0, validTaskId, null);
            fail("Zero task id should throw an exception");
        } catch (IllegalArgumentException ex) {
        }

        try {
            tasksService.moveTask(-1, -1, null);
            fail("Negative new parent task id and task id should throw an exception");
        } catch (IllegalArgumentException ex) {
        }
    }

    /**
     * Creates a new task with the specified projectId.
     *
     * @param projectId Project id for the task.
     * @return The created task.
     */
    private Task createTask(int projectId) {
        Task task = new Task();
        task.setProjectId(projectId);

        return task;
    }
}
