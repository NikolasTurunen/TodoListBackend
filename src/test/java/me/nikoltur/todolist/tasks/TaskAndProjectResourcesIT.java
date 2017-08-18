package me.nikoltur.todolist.tasks;

import java.util.List;
import me.nikoltur.todolist.Application;
import me.nikoltur.todolist.DatabaseWiper;
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
}
