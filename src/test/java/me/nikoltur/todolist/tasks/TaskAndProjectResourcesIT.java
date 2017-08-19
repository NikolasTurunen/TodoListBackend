package me.nikoltur.todolist.tasks;

import java.util.List;
import me.nikoltur.todolist.Application;
import me.nikoltur.todolist.DatabaseWiper;
import me.nikoltur.todolist.projects.ProjectDoesNotExistException;
import me.nikoltur.todolist.projects.ProjectHasTasksException;
import me.nikoltur.todolist.projects.ProjectsResource;
import me.nikoltur.todolist.projects.da.Project;
import me.nikoltur.todolist.tasks.da.Task;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
public class TaskAndProjectResourcesIT {

    @Autowired
    private DatabaseWiper databaseWiper;
    @Autowired
    private ProjectsResource projectsResource;
    @Autowired
    private TasksResource tasksResource;

    @Before
    public void setUp() {
        databaseWiper.wipeDatabase();
    }

    @Test
    public void testCreateTaskForProject() {
        projectsResource.createProject("Project");

        String taskString = "Do this and do that";
        Project project = projectsResource.getProjects().get(0);

        Assert.assertEquals("Size of tasks should be initially zero", 0, tasksResource.getTasks(project.getId()).size());

        tasksResource.createTask(project.getId(), taskString);

        List<Task> tasks = tasksResource.getTasks(project.getId());
        Assert.assertEquals("Size of tasks should be 1 after creation", 1, tasks.size());

        Task task = tasks.get(0);
        Assert.assertEquals("Id of the single task should be 1", 1, task.getId());
        Assert.assertEquals("Project id of the single task should match the created task", project.getId(), task.getProjectId());
        Assert.assertEquals("Task string of the single task should match the created task", taskString, task.getTaskString());
    }

    @Test(expected = ProjectDoesNotExistException.class)
    public void testCreateTaskForProjectThatDoesNotExist() {
        tasksResource.createTask(123, "Test");
    }

    @Test(expected = ProjectHasTasksException.class)
    public void testProjectRemovalWithTasks() {
        projectsResource.createProject("Myproject");
        Project project = projectsResource.getProjects().get(0);
        tasksResource.createTask(project.getId(), "Mytask");
        projectsResource.removeProject(project.getName());
    }

    @Test
    public void testRemoveTask() {
        projectsResource.createProject("Myproject");
        Project project = projectsResource.getProjects().get(0);
        tasksResource.createTask(project.getId(), "Mytask");

        List<Task> tasks = tasksResource.getTasks(project.getId());

        tasksResource.removeTask(tasks.get(0).getId());

        List<Task> tasksAfterRemoval = tasksResource.getTasks(project.getId());

        Assert.assertTrue("Tasks should be empty after the created task was removed", tasksAfterRemoval.isEmpty());
    }

    @Test
    public void testRemoveTaskWithMultipleTasks() {
        String taskStringForRemoval = "Mytask";
        String taskStringNotForRemoval = "Mytask2";

        projectsResource.createProject("Myproject");
        Project project = projectsResource.getProjects().get(0);
        tasksResource.createTask(project.getId(), taskStringForRemoval);
        tasksResource.createTask(project.getId(), taskStringNotForRemoval);

        List<Task> tasks = tasksResource.getTasks(project.getId());
        for (Task task : tasks) {
            if (task.getTaskString().equals(taskStringForRemoval)) {
                tasksResource.removeTask(task.getId());
            }
        }

        List<Task> tasksAfterRemoval = tasksResource.getTasks(project.getId());

        Assert.assertEquals("One task should remain", 1, tasksAfterRemoval.size());
        Assert.assertEquals("Task string of the remaining task should match the task that was not deleted", taskStringNotForRemoval, tasksAfterRemoval.get(0).getTaskString());
    }

    @Test(expected = TaskDoesNotExistException.class)
    public void testRemoveTaskThrowsIfTaskDoesNotExist() {
        tasksResource.removeTask(1);
    }
}
