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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
}
