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
public class ProjectsServiceTest {

    @Autowired
    @InjectMocks
    private ProjectsService projectsService;
    @Autowired
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
    public void testCreateProject() {
        ArgumentCaptor<Project> argumentCaptor = ArgumentCaptor.forClass(Project.class);
        Mockito.doNothing().when(projectsDao).save(argumentCaptor.capture());

        String projectName = "Test";

        projectsService.createProject(projectName);
        Mockito.verify(projectsDao, times(1)).save(anyObject());
        Assert.assertEquals("Project name should be equal to the specified", projectName, argumentCaptor.getValue().getName());
    }
}
