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
        int projectId = 1;
        String newProjectName = "NewProject1";
        projectsResource.renameProject(projectId, newProjectName);

        Mockito.verify(projectsService, times(1)).renameProject(projectId, newProjectName);
    }

    @Test
    public void testSwapPositions() {
        int projectId = 1;
        int projectId2 = 2;

        projectsResource.swapPositionsOfProjects(projectId, projectId2);

        Mockito.verify(projectsService, times(1)).swapPositionsOfProjects(projectId, projectId2);
    }
}
