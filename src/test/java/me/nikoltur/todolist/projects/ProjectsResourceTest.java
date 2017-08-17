package me.nikoltur.todolist.projects;

import me.nikoltur.todolist.Application;
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
    public void testCreateProject() {
        String projectName = "Testing";
        projectsResource.createProject(projectName);

        Mockito.verify(projectsService, times(1)).createProject(projectName);
    }
}
