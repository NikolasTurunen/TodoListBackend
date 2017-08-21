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
        Project project = createProject("Project");

        String taskString = "Do this and do that";

        Assert.assertEquals("Size of tasks should be initially zero", 0, tasksResource.getTasks(project.getId()).size());

        tasksResource.createTask(project.getId(), taskString);

        List<Task> tasks = tasksResource.getTasks(project.getId());
        Assert.assertEquals("Size of tasks should be 1 after creation", 1, tasks.size());

        Task task = tasks.get(0);
        Assert.assertEquals("Id of the single task should be 1", 1, task.getId());
        Assert.assertEquals("Project id of the single task should match the created task", project.getId(), (int) task.getProjectId());
        Assert.assertEquals("Task string of the single task should match the created task", taskString, task.getTaskString());
    }

    @Test(expected = ProjectDoesNotExistException.class)
    public void testCreateTaskForProjectThatDoesNotExist() {
        tasksResource.createTask(123, "Test");
    }

    @Test(expected = ProjectHasTasksException.class)
    public void testProjectRemovalWithTasks() {
        Project project = createProject("Project");
        tasksResource.createTask(project.getId(), "Mytask");
        projectsResource.removeProject(project.getName());
    }

    @Test
    public void testRemoveTask() {
        Project project = createProject("Project");

        Task task = createTask(project.getId(), "Mytask");

        tasksResource.removeTask(task.getId());

        List<Task> tasksAfterRemoval = tasksResource.getTasks(project.getId());

        Assert.assertTrue("Tasks should be empty after the created task was removed", tasksAfterRemoval.isEmpty());
    }

    @Test
    public void testRemoveTaskWithMultipleTasks() {
        String taskStringForRemoval = "Mytask";
        String taskStringNotForRemoval = "Mytask2";

        Project project = createProject("Project");
        Task taskForRemoval = createTask(project.getId(), taskStringForRemoval);
        createTask(project.getId(), taskStringNotForRemoval);

        tasksResource.removeTask(taskForRemoval.getId());

        List<Task> tasksAfterRemoval = tasksResource.getTasks(project.getId());

        Assert.assertEquals("One task should remain", 1, tasksAfterRemoval.size());
        Assert.assertEquals("Task string of the remaining task should match the task that was not deleted", taskStringNotForRemoval, tasksAfterRemoval.get(0).getTaskString());
    }

    @Test(expected = TaskDoesNotExistException.class)
    public void testRemoveTaskThrowsIfTaskDoesNotExist() {
        tasksResource.removeTask(1);
    }

    @Test
    public void testProjectAndTaskCreateAndRemove() {
        Project project = createProject("Project");
        Task task = createTask(project.getId(), "Task");
        tasksResource.removeTask(task.getId());
        projectsResource.removeProject(project.getName());

        Assert.assertTrue("Projects should be empty after removal of task and project", projectsResource.getProjects().isEmpty());
    }

    @Test
    public void testTasksAreCorrectlyUnderProjects() {
        final String project1Name = "Project";
        final String project2Name = "Project2";

        projectsResource.createProject(project1Name);
        projectsResource.createProject(project2Name);

        List<Project> projects = projectsResource.getProjects();

        Project project1 = null;
        Project project2 = null;

        for (Project project : projects) {
            switch (project.getName()) {
                case project1Name:
                    project1 = project;
                    break;
                case project2Name:
                    project2 = project;
                    break;
            }
        }

        if (project1 == null || project2 == null) {
            Assert.fail("Null project");
            return;
        }

        String task1Name = "Task1";
        String task2Name = "Task2";

        tasksResource.createTask(project1.getId(), task1Name);
        tasksResource.createTask(project2.getId(), task2Name);

        List<Task> tasks1 = tasksResource.getTasks(project1.getId());
        Assert.assertEquals("First project should have 1 task", 1, tasks1.size());
        Assert.assertEquals("The single task of first project should have the specified task string", task1Name, tasks1.get(0).getTaskString());

        List<Task> tasks2 = tasksResource.getTasks(project2.getId());
        Assert.assertEquals("Second project should have 1 task", 1, tasks2.size());
        Assert.assertEquals("The single task of second project should have the specified task string", task2Name, tasks2.get(0).getTaskString());
    }

    @Test
    public void testEditTask() {
        String newTask = "Do this instead";

        Project project = createProject("Project");
        Task task = createTask(project.getId(), "Task");
        tasksResource.editTask(task.getId(), newTask);

        Task editedTask = tasksResource.getTasks(project.getId()).get(0);
        Assert.assertEquals("Edited task should have the new task string", newTask, editedTask.getTaskString());
    }

    @Test(expected = TaskDoesNotExistException.class)
    public void testEditTaskThrowsIfTaskDoesNotExist() {
        tasksResource.editTask(1, "Does not matter");
    }

    @Test
    public void testDetailsIsInitiallyEmpty() {
        Project project = createProject("Project");
        Task task = createTask(project.getId(), "Task");

        Assert.assertTrue("Details of task should initially be empty", task.getDetails().isEmpty());
    }

    @Test
    public void testCreateDetailAndGetTasksDoesNotReturnDetailDirectly() {
        String taskString = "Task";

        Project project = createProject("Project");
        Task task = createTask(project.getId(), taskString);
        tasksResource.createDetail(task.getId(), "Task detail");
        List<Task> tasks = tasksResource.getTasks(project.getId());
        Assert.assertEquals("Size of tasks should be 1 because it should not contain the created detail", 1, tasks.size());
        Assert.assertEquals("The single task should be the created task", taskString, tasks.get(0).getTaskString());
    }

    @Test
    public void testCreatedDetailIsInDetails() {
        String detailString = "Task detail";

        Project project = createProject("Project");
        Task task = createTask(project.getId(), "Task");
        tasksResource.createDetail(task.getId(), detailString);
        List<Task> details = task.getDetails();
        Assert.assertEquals("Size of details should be 1 after creation of a single detail for the task", 1, details.size());
        Assert.assertEquals("The task string of the single detail should equal the created detail", detailString, details.get(0).getTaskString());
    }

    @Test
    public void testCreateMultipleDetails() {
        Project project = createProject("Project");
        Task task = createTask(project.getId(), "Task");
        createDetail(task, "Detail1");
        createDetail(task, "Detail2");
        createDetail(task, "Detail3");
        createDetail(task, "Detail4");

        Assert.assertEquals("Should contain 4 details", 4, tasksResource.getTasks(project.getId()).get(0).getDetails().size());
    }

    @Test
    public void testDetailCanBeRemoved() {
        Project project = createProject("Project");
        Task task = createTask(project.getId(), "Task");
        Task detail = createDetail(task, "Detail");
        tasksResource.removeTask(detail.getId());

        Task updatedTask = tasksResource.getTasks(project.getId()).get(0);
        Assert.assertTrue("Details of task should be empty after the single detail is removed", updatedTask.getDetails().isEmpty());
    }

    @Test
    public void testDetailCanBeEdited() {
        String newDetailString = "NewDetail";

        Project project = createProject("Project");
        Task task = createTask(project.getId(), "Task");
        Task detail = createDetail(task, "Detail");
        tasksResource.editTask(detail.getId(), newDetailString);

        Task updatedTask = tasksResource.getTasks(project.getId()).get(0);
        Assert.assertEquals("Detail should have the new task string after edit", newDetailString, updatedTask.getDetails().get(0).getTaskString());
    }

    @Test
    public void testTaskCanBeRemovedWithDetails() {
        Project project = createProject("Project");
        Task task = createTask(project.getId(), "Task");
        createDetail(task, "Detail");
        createDetail(task, "Detail2");
        createDetail(task, "Detail3");
        tasksResource.removeTask(task.getId());

        Assert.assertTrue("Task should be removed even if if it has details", tasksResource.getTasks(project.getId()).isEmpty());
    }

    /**
     * Creates a project with the specified name and returns the created project.
     *
     * @param name Name of the project to be created.
     * @return The created project.
     */
    private Project createProject(String name) {
        projectsResource.createProject(name);
        for (Project project : projectsResource.getProjects()) {
            if (project.getName().equals(name)) {
                return project;
            }
        }
        throw new IllegalStateException("The created project was not found");
    }

    /**
     * Creates a task for the specified project with the specified taskString and returns the created task.
     *
     * @param projectId Id of the project to create the task for.
     * @param taskString Task string for the task.
     * @return The created task.
     */
    private Task createTask(int projectId, String taskString) {
        tasksResource.createTask(projectId, taskString);
        for (Task task : tasksResource.getTasks(projectId)) {
            if (task.getTaskString().equals(taskString)) {
                return task;
            }
        }

        throw new IllegalStateException("The created task was not found");
    }

    /**
     * Creates a detail for the specified task with the specified detailString and returns the created detail.
     *
     * @param task Task for the detail to be created for.
     * @param detailString Task string for the detail
     * @return The created detail.
     */
    private Task createDetail(Task task, String detailString) {
        tasksResource.createDetail(task.getId(), detailString);

        List<Task> tasks = tasksResource.getTasks(task.getProjectId());

        for (Task updatedTask : tasks) {
            if (updatedTask.getId() == task.getId()) {
                for (Task detail : updatedTask.getDetails()) {
                    if (detail.getTaskString().equals(detailString)) {
                        return detail;
                    }
                }
            }
        }

        throw new IllegalStateException("The created detail was not found");
    }
}
