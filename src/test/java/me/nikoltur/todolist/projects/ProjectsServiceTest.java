package me.nikoltur.todolist.projects;

import java.util.ArrayList;
import java.util.List;
import me.nikoltur.todolist.Application;
import me.nikoltur.todolist.projects.da.Project;
import me.nikoltur.todolist.projects.da.ProjectsDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
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
    public void testRemoveProject() {
        String projectName = "Testit";

        Project project = new Project();
        project.setName(projectName);

        Mockito.doReturn(project).when(projectsDao).getByName(projectName);

        ArgumentCaptor<Project> argumentCaptor = ArgumentCaptor.forClass(Project.class);
        Mockito.doNothing().when(projectsDao).remove(argumentCaptor.capture());

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
}
