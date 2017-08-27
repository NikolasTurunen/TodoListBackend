package me.nikoltur.todolist.projects;

import java.util.ArrayList;
import java.util.List;
import me.nikoltur.todolist.Application;
import me.nikoltur.todolist.projects.da.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.times;
import org.mockito.MockitoAnnotations;
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
public class ProjectsResourceTest {

    @InjectMocks
    private ProjectsResource projectsResource;
    @Autowired
    @Mock
    private ProjectsService projectsService;

    @Before
    public void setUp() {
        projectsResource = new ProjectsResource();

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetProjects() {
        List<Project> list = new ArrayList<>();
        Mockito.when(projectsService.getProjects()).thenReturn(list);

        List<Project> projects = projectsResource.getProjects();

        Assert.assertSame("Returned list should match the list that is returned from the service", list, projects);
    }

    @Test
    public void testCreateProject() {
        String projectName = "Testing";
        projectsResource.createProject(projectName);

        Mockito.verify(projectsService, times(1)).createProject(projectName);
    }

    @Test
    public void testRemoveProject() {
        int projectId = 1;
        projectsResource.removeProject(projectId);

        Mockito.verify(projectsService, times(1)).removeProject(projectId);
    }

    @Test
    public void testRenameProject() {
        String projectName = "Project1";
        String newProjectName = "NewProject1";
        projectsResource.renameProject(projectName, newProjectName);

        Mockito.verify(projectsService, times(1)).renameProject(projectName, newProjectName);
    }

    @Test
    public void testSwapPositions() {
        String projectName1 = "Project1";
        String projectName2 = "Project2";

        projectsResource.swapPositionsOfProjects(projectName1, projectName2);

        Mockito.verify(projectsService, times(1)).swapPositionsOfProjects(projectName1, projectName2);
    }
}
