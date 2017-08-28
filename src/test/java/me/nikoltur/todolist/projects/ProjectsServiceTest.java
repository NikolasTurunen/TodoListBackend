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

    @Test(expected = IllegalArgumentException.class)
    public void testCreateProjectThrowsForWhitespaceOnlyName() {
        projectsService.createProject(" ");
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
        int projectId = 1;
        String projectName = "Testit";

        Project project = new Project();
        project.setName(projectName);

        Mockito.doReturn(project).when(projectsDao).getById(projectId);

        ArgumentCaptor<Project> argumentCaptor = ArgumentCaptor.forClass(Project.class);
        Mockito.doNothing().when(projectsDao).remove(argumentCaptor.capture());

        Mockito.doReturn(new ArrayList<>()).when(tasksDao).getAllOf(project.getId());

        projectsService.removeProject(projectId);
        Mockito.verify(projectsDao, times(1)).remove(anyObject());
        Assert.assertEquals("Project name should be equal to the specified", projectName, argumentCaptor.getValue().getName());
    }

    @Test
    public void testRemoveProjectThrowsWhenProjectDoesNotExist() {
        int projectId = 1;
        Mockito.doReturn(null).when(projectsDao).getById(projectId);
        Mockito.doNothing().when(projectsDao).remove(anyObject());

        try {
            projectsService.removeProject(projectId);
            Assert.fail(); // Fail if no exception is caught.
        } catch (ProjectDoesNotExistException ex) {
            Assert.assertSame("Exception type should be ProjectDoesNotExistException", ProjectDoesNotExistException.class, ex.getClass());
        }
    }

    @Test
    public void testRemoveProjectThrowsForIllegalId() {
        try {
            projectsService.removeProject(0);
            Assert.fail("Should throw for zero id");
        } catch (IllegalArgumentException ex) {
        }

        try {
            projectsService.removeProject(-1);
            Assert.fail("Should throw for negative id");
        } catch (IllegalArgumentException ex) {
        }

        try {
            projectsService.removeProject(-265);
            Assert.fail("Should throw for negative id");
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test(expected = ProjectHasTasksException.class)
    public void testRemoveProjectThrowsIfProjectHasTasks() {
        int projectId = 1;

        Project project = new Project();
        project.setName("Project name");

        Mockito.doReturn(project).when(projectsDao).getById(projectId);

        List<Task> tasks = new ArrayList<>();
        Task task = new Task();
        task.setProjectId(1);
        task.setTaskString("Task string");
        tasks.add(task);

        Mockito.doReturn(tasks).when(tasksDao).getAllOf(anyInt());
        projectsService.removeProject(projectId);
    }

    @Test
    public void testRenameProject() {
        int projectId = 1;
        String newProjectName = "New Project name";

        Project project = new Project();
        project.setName("Project");

        Mockito.doReturn(project).when(projectsDao).getById(projectId);

        ArgumentCaptor<Project> argumentCaptor = ArgumentCaptor.forClass(Project.class);
        Mockito.doNothing().when(projectsDao).save(argumentCaptor.capture());

        projectsService.renameProject(projectId, newProjectName);

        Mockito.verify(projectsDao, times(1)).save(anyObject());
        Assert.assertSame("Saved project should be the same as specified", project, argumentCaptor.getValue());
        Assert.assertEquals("Saved project should have the new name", newProjectName, argumentCaptor.getValue().getName());
    }

    @Test(expected = NullPointerException.class)
    public void testRenameProjectThrowsForNullName() {

        Project project = new Project();
        project.setName("Project");

        Mockito.doReturn(project).when(projectsDao).getById(anyInt());

        projectsService.renameProject(1, null);
    }

    @Test
    public void testRenameProjectThrowsForEmptyOrWhitespaceOnlyName() {
        Project project = new Project();
        project.setName("Project");

        Mockito.doReturn(project).when(projectsDao).getById(anyInt());

        try {
            projectsService.renameProject(1, "");
            Assert.fail("Should throw IllegalArgumentException for empty newName");
        } catch (IllegalArgumentException ex) {
        }

        try {
            projectsService.renameProject(1, " ");
            Assert.fail("Should throw IllegalArgumentException for space-only newName");
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test(expected = ProjectDoesNotExistException.class)
    public void testRenameProjectThrowsWhenProjectDoesNotExist() {
        int projectId = 1;

        Mockito.doReturn(null).when(projectsDao).getById(projectId);

        projectsService.renameProject(projectId, "New name");
    }

    @Test(expected = ProjectAlreadyExistsException.class)
    public void testRenameProjectThrowsWhenProjectAlreadyExists() {
        int projectId = 1;
        String newProjectName = "New Project";

        Project project = new Project();
        project.setName("Project");
        Mockito.doReturn(project).when(projectsDao).getById(projectId);

        Project alreadyExistingProject = new Project();
        alreadyExistingProject.setName(newProjectName);
        Mockito.doReturn(alreadyExistingProject).when(projectsDao).getByName(newProjectName);

        projectsService.renameProject(projectId, newProjectName);
    }

    @Test
    public void testSwapPositionOfProjects() {
        int position1 = 1;
        int position2 = 2;

        int projectId1 = 1;
        int projectId2 = 2;

        Project project1 = new Project();
        project1.setName("Project1");
        project1.setPosition(position1);

        Project project2 = new Project();
        project2.setName("Project2");
        project2.setPosition(position2);

        Mockito.doReturn(project1).when(projectsDao).getById(projectId1);
        Mockito.doReturn(project2).when(projectsDao).getById(projectId2);

        ArgumentCaptor<Project> argumentCaptor = ArgumentCaptor.forClass(Project.class);
        Mockito.doNothing().when(projectsDao).save(argumentCaptor.capture());

        projectsService.swapPositionsOfProjects(projectId1, projectId2);

        Mockito.verify(projectsDao, times(2)).save(anyObject());
        List<Project> capturedArguments = argumentCaptor.getAllValues();
        for (Project argument : capturedArguments) {
            if (argument == project1) {
                Assert.assertEquals("Position of saved project1 should be the position of project2", position2, argument.getPosition());
            } else if (argument == project2) {
                Assert.assertEquals("Position of saved project2 should be the position of project1", position1, argument.getPosition());
            } else {
                Assert.fail("Should only save the specified projects");
            }
        }
    }

    @Test
    public void testSwapPositionOfProjectsThrowsForInvalidId() {
        try {
            projectsService.swapPositionsOfProjects(0, 1);
            Assert.fail("Should throw IllegalArgumentException for empty name");
        } catch (IllegalArgumentException ex) {
        }

        try {
            projectsService.swapPositionsOfProjects(1, 0);
            Assert.fail("Should throw IllegalArgumentException for empty name2");
        } catch (IllegalArgumentException ex) {
        }

        try {
            projectsService.swapPositionsOfProjects(0, 0);
            Assert.fail("Should throw IllegalArgumentException when both name and name2 are empty");
        } catch (IllegalArgumentException ex) {
        }

        try {
            projectsService.swapPositionsOfProjects(-1, 1);
            Assert.fail("Should throw IllegalArgumentException for empty name");
        } catch (IllegalArgumentException ex) {
        }

        try {
            projectsService.swapPositionsOfProjects(1, -1);
            Assert.fail("Should throw IllegalArgumentException for empty name2");
        } catch (IllegalArgumentException ex) {
        }

        try {
            projectsService.swapPositionsOfProjects(-1, -1);
            Assert.fail("Should throw IllegalArgumentException when both name and name2 are empty");
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSwapPositionOfProjectsThrowsForEqualIds() {
        projectsService.swapPositionsOfProjects(1, 1);
    }

    @Test
    public void testSwapPositionOfProjectsThrowsForNonExistingProject() {
        int projectId1 = 1;
        int projectId2 = 2;

        Project project = new Project();
        project.setName("Name");
        Mockito.doReturn(project).when(projectsDao).getById(projectId1);
        Mockito.doReturn(null).when(projectsDao).getById(projectId2);

        try {
            projectsService.swapPositionsOfProjects(projectId1, projectId2);
            Assert.fail("Should throw ProjectDoesNotExistException if first project does not exist");
        } catch (ProjectDoesNotExistException ex) {
        }

        try {
            projectsService.swapPositionsOfProjects(projectId2, projectId1);
            Assert.fail("Should throw ProjectDoesNotExistException if second project does not exist");
        } catch (ProjectDoesNotExistException ex) {
        }

        Mockito.doReturn(null).when(projectsDao).getById(projectId1);

        try {
            projectsService.swapPositionsOfProjects(projectId1, projectId2);
            Assert.fail("Should throw ProjectDoesNotExistException if neither project does not exist");
        } catch (ProjectDoesNotExistException ex) {
        }
    }

    @Test
    public void testCreateProjectSetsPositionOfZeroForFirstProject() {
        Mockito.doReturn(new ArrayList<>()).when(projectsDao).getAll();

        ArgumentCaptor<Project> argumentCaptor = ArgumentCaptor.forClass(Project.class);
        Mockito.doNothing().when(projectsDao).save(argumentCaptor.capture());

        projectsService.createProject("Project");

        Assert.assertEquals("Position of zero should be assigned to the first project", 0, argumentCaptor.getValue().getPosition());
    }

    @Test
    public void testCreateProjectSetsPositionOfOneForSecondProject() {
        List<Project> projects = new ArrayList<>();
        projects.add(new Project());
        Mockito.doReturn(projects).when(projectsDao).getAll();

        ArgumentCaptor<Project> argumentCaptor = ArgumentCaptor.forClass(Project.class);
        Mockito.doNothing().when(projectsDao).save(argumentCaptor.capture());

        projectsService.createProject("Project");

        Assert.assertEquals("Position of 1 should be assigned to the second project", 1, argumentCaptor.getValue().getPosition());
    }

    @Test
    public void testRemoveProjectUpdatesPositions() {
        List<Project> projects = new ArrayList<>();

        int projectIdToRemove = 1;

        Project projectToRemove = new Project();
        projectToRemove.setName("Project to remove");
        projectToRemove.setPosition(1);

        Project project1 = new Project();
        project1.setName("Project1");
        project1.setPosition(0);
        projects.add(project1);

        Project project3 = new Project();
        project3.setName("Project3");
        project3.setPosition(2);
        projects.add(project3);

        Project project4 = new Project();
        project4.setName("Project4");
        project4.setPosition(3);
        projects.add(project4);

        ArgumentCaptor<Project> argumentCaptor = ArgumentCaptor.forClass(Project.class);
        Mockito.doNothing().when(projectsDao).save(argumentCaptor.capture());

        Mockito.doReturn(projectToRemove).when(projectsDao).getById(projectIdToRemove);

        Mockito.doReturn(projects).when(projectsDao).getAll();

        projectsService.removeProject(projectIdToRemove);

        Mockito.verify(projectsDao, times(2)).save(anyObject());

        for (Project argument : argumentCaptor.getAllValues()) {
            if (argument == project3) {
                Assert.assertEquals("Position of project 3 should be saved to 1", 1, argument.getPosition());
            } else if (argument == project4) {
                Assert.assertEquals("Position of project 4 should be saved to 2", 2, argument.getPosition());
            } else {
                Assert.fail("Should not save any other projects");
            }
        }
    }

    @Test
    public void testRemoveProjectThatIsLastDoesNotSaveAnything() {
        int projectId = 1;
        Project project = new Project();
        project.setName("Name");

        Mockito.doReturn(project).when(projectsDao).getById(projectId);

        Mockito.doReturn(new ArrayList<>()).when(projectsDao).getAll();

        projectsService.removeProject(projectId);

        Mockito.verify(projectsDao, times(0)).save(anyObject());
    }
}
