package me.nikoltur.todolist.projects;

import java.util.ArrayList;
import java.util.List;
import me.nikoltur.todolist.Application;
import me.nikoltur.todolist.projects.da.Project;
import me.nikoltur.todolist.projects.da.ProjectsDao;
import me.nikoltur.todolist.tasks.da.Task;
import me.nikoltur.todolist.tasks.da.TasksDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
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
public class ProjectsServiceTest {

    @InjectMocks
    private ProjectsService projectsService = new ProjectsServiceImpl();
    @Mock
    private ProjectsDao projectsDao;
    @Mock
    private TasksDao tasksDao;

    public ProjectsServiceTest() {
    }

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetProjects() {
        List<Project> projects = new ArrayList<>();
        Project project = new Project();
        project.setName("Test");
        projects.add(project);

        Mockito.doReturn(projects).when(projectsDao).getAll();

        Assert.assertEquals("Size should be 1", 1, projectsService.getProjects().size());

        Assert.assertSame("Project should match", project, projectsService.getProjects().get(0));
    }

    @Test
    public void testCreateProjectCreatesProject() {
        ArgumentCaptor<Project> argumentCaptor = ArgumentCaptor.forClass(Project.class);
        Mockito.doNothing().when(projectsDao).save(argumentCaptor.capture());

        String projectName = "Test";

        projectsService.createProject(projectName);
        Mockito.verify(projectsDao, times(1)).save(anyObject());
        Assert.assertEquals("Project name should be equal to the specified", projectName, argumentCaptor.getValue().getName());
    }

    @Test(expected = NullPointerException.class)
    public void testCreateProjectThrowsForNullName() {
        projectsService.createProject(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreateProjectThrowsForEmptyName() {
        projectsService.createProject("");
    }

    @Test
    public void testCreateProjectThrowsIfProjectAlreadyExists() {
        String projectName = "Testtt";

        Project project = new Project();
        project.setName(projectName);

        projectsService.createProject(projectName);
        Mockito.doReturn(project).when(projectsDao).getByName(projectName);
        try {
            projectsService.createProject(projectName);
            Assert.fail();
        } catch (ProjectAlreadyExistsException ex) {
            Assert.assertSame("Should throw ProjectAlreadyExistsException if a project with the same name already exists", ProjectAlreadyExistsException.class, ex.getClass());
        }
    }

    @Test
    public void testRemoveProject() {
        String projectName = "Testit";

        Project project = new Project();
        project.setName(projectName);

        Mockito.doReturn(project).when(projectsDao).getByName(projectName);

        ArgumentCaptor<Project> argumentCaptor = ArgumentCaptor.forClass(Project.class);
        Mockito.doNothing().when(projectsDao).remove(argumentCaptor.capture());

        Mockito.doReturn(new ArrayList<>()).when(tasksDao).getAllOf(project.getId());

        projectsService.removeProject(projectName);
        Mockito.verify(projectsDao, times(1)).remove(anyObject());
        Assert.assertEquals("Project name should be equal to the specified", projectName, argumentCaptor.getValue().getName());
    }

    @Test
    public void testRemoveProjectThrowsWhenProjectDoesNotExist() {
        String projectName = "Testa";
        Mockito.doReturn(null).when(projectsDao).getByName(projectName);
        Mockito.doNothing().when(projectsDao).remove(anyObject());

        try {
            projectsService.removeProject(projectName);
            Assert.fail(); // Fail if no exception is caught.
        } catch (ProjectDoesNotExistException ex) {
            Assert.assertSame("Exception type should be ProjectDoesNotExistException", ProjectDoesNotExistException.class, ex.getClass());
        }
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveProjectThrowsForNullName() {
        projectsService.removeProject(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveProjectThrowsForEmptyName() {
        projectsService.removeProject("");
    }

    @Test(expected = ProjectHasTasksException.class)
    public void testRemoveProjectThrowsIfProjectHasTasks() {
        String projectName = "Project name";

        Project project = new Project();
        project.setName(projectName);

        Mockito.doReturn(project).when(projectsDao).getByName(projectName);

        List<Task> tasks = new ArrayList<>();
        Task task = new Task();
        task.setProjectId(1);
        task.setTaskString("Task string");
        tasks.add(task);

        Mockito.doReturn(tasks).when(tasksDao).getAllOf(anyInt());
        projectsService.removeProject(projectName);
    }
}
