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
import static org.mockito.Matchers.anyString;
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

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveProjectThrowsForWhitespaceOnlyName() {
        projectsService.removeProject(" ");
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

    @Test
    public void testRenameProject() {
        String projectName = "Project";
        String newProjectName = "New Project name";

        Project project = new Project();
        project.setName(projectName);

        Mockito.doReturn(project).when(projectsDao).getByName(projectName);

        ArgumentCaptor<Project> argumentCaptor = ArgumentCaptor.forClass(Project.class);
        Mockito.doNothing().when(projectsDao).save(argumentCaptor.capture());

        projectsService.renameProject(projectName, newProjectName);

        Mockito.verify(projectsDao, times(1)).save(anyObject());
        Assert.assertSame("Saved project should be the same as returned from getByName()", project, argumentCaptor.getValue());
        Assert.assertEquals("Saved project should have the new name", newProjectName, argumentCaptor.getValue().getName());
    }

    @Test
    public void testRenameProjectThrowsForNullName() {

        Project project = new Project();
        project.setName("Project");

        Mockito.doReturn(project).when(projectsDao).getByName(anyString());

        try {
            projectsService.renameProject(null, "New name");
            Assert.fail("Should throw NullPointerException for null name");
        } catch (NullPointerException ex) {
        }

        try {
            projectsService.renameProject("Name", null);
            Assert.fail("Should throw NullPointerException for null newName");
        } catch (NullPointerException ex) {
        }

        try {
            projectsService.renameProject(null, null);
            Assert.fail("Should throw NullPointerException for both null name and null newName");
        } catch (NullPointerException ex) {
        }
    }

    @Test
    public void testRenameProjectThrowsForEmptyName() {
        Project project = new Project();
        project.setName("Project");

        Mockito.doReturn(project).when(projectsDao).getByName(anyString());

        try {
            projectsService.renameProject("", "New name");
            Assert.fail("Should throw IllegalArgumentException for empty name");
        } catch (IllegalArgumentException ex) {
        }

        try {
            projectsService.renameProject("Name", "");
            Assert.fail("Should throw IllegalArgumentException for empty newName");
        } catch (IllegalArgumentException ex) {
        }

        try {
            projectsService.renameProject("", "");
            Assert.fail("Should throw IllegalArgumentException for both empty name and empty newName");
        } catch (IllegalArgumentException ex) {
        }

        try {
            projectsService.renameProject(" ", "New name");
            Assert.fail("Should throw IllegalArgumentException for space-only name");
        } catch (IllegalArgumentException ex) {
        }

        try {
            projectsService.renameProject("Name", " ");
            Assert.fail("Should throw IllegalArgumentException for space-only newName");
        } catch (IllegalArgumentException ex) {
        }

        try {
            projectsService.renameProject(" ", " ");
            Assert.fail("Should throw IllegalArgumentException for space-only both name and newName");
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test(expected = ProjectDoesNotExistException.class)
    public void testRenameProjectThrowsWhenProjectDoesNotExist() {
        String projectName = "Project";

        Mockito.doReturn(null).when(projectsDao).getByName(projectName);

        projectsService.renameProject(projectName, "New name");
    }

    @Test(expected = ProjectAlreadyExistsException.class)
    public void testRenameProjectThrowsWhenProjectAlreadyExists() {
        String projectName = "Project";
        String newProjectName = "New Project";

        Project project = new Project();
        project.setName(projectName);
        Mockito.doReturn(project).when(projectsDao).getByName(projectName);

        Project alreadyExistingProject = new Project();
        alreadyExistingProject.setName(newProjectName);
        Mockito.doReturn(alreadyExistingProject).when(projectsDao).getByName(newProjectName);

        projectsService.renameProject(projectName, newProjectName);
    }

    @Test
    public void testSwapPositionOfProjects() {
        int position1 = 1;
        int position2 = 2;

        String projectName1 = "Project1";
        String projectName2 = "Project2";

        Project project1 = new Project();
        project1.setName(projectName1);
        project1.setPosition(position1);

        Project project2 = new Project();
        project2.setName(projectName2);
        project2.setPosition(position2);

        Mockito.doReturn(project1).when(projectsDao).getByName(projectName1);
        Mockito.doReturn(project2).when(projectsDao).getByName(projectName2);

        ArgumentCaptor<Project> argumentCaptor = ArgumentCaptor.forClass(Project.class);
        Mockito.doNothing().when(projectsDao).save(argumentCaptor.capture());

        projectsService.swapPositionsOfProjects(projectName1, projectName2);

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
    public void testSwapPostionOfProjectsThrowsForNullName() {
        try {
            projectsService.swapPositionsOfProjects(null, "Name2");
            Assert.fail("Should throw NullPointerException for null name");
        } catch (NullPointerException ex) {
        }

        try {
            projectsService.swapPositionsOfProjects("Name1", null);
            Assert.fail("Should throw NullPointerException for null name2");
        } catch (NullPointerException ex) {
        }

        try {
            projectsService.swapPositionsOfProjects(null, null);
            Assert.fail("Should throw NullPointerException when both name and name2 are null");
        } catch (NullPointerException ex) {
        }
    }

    @Test
    public void testSwapPositionOfProjectsThrowsForInvalidName() {
        try {
            projectsService.swapPositionsOfProjects("", "Name2");
            Assert.fail("Should throw IllegalArgumentException for empty name");
        } catch (IllegalArgumentException ex) {
        }

        try {
            projectsService.swapPositionsOfProjects("Name1", "");
            Assert.fail("Should throw IllegalArgumentException for empty name2");
        } catch (IllegalArgumentException ex) {
        }

        try {
            projectsService.swapPositionsOfProjects("", "");
            Assert.fail("Should throw IllegalArgumentException when both name and name2 are empty");
        } catch (IllegalArgumentException ex) {
        }

        try {
            projectsService.swapPositionsOfProjects(" ", "Name2");
            Assert.fail("Should throw IllegalArgumentException for whitespace-only name");
        } catch (IllegalArgumentException ex) {
        }

        try {
            projectsService.swapPositionsOfProjects("Name1", " ");
            Assert.fail("Should throw IllegalArgumentException for whitespace-only name2");
        } catch (IllegalArgumentException ex) {
        }

        try {
            projectsService.swapPositionsOfProjects(" ", " ");
            Assert.fail("Should throw IllegalArgumentException when both name and name2 are whitespace-only");
        } catch (IllegalArgumentException ex) {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSwapPositionOfProjectsThrowsForEqualNames() {
        projectsService.swapPositionsOfProjects("Name", "Name");
    }

    @Test
    public void testSwapPositionOfProjectsThrowsForNonExistingProject() {
        String projectName = "Name";
        String projectName2 = "Name2";

        Project project = new Project();
        project.setName(projectName);
        Mockito.doReturn(project).when(projectsDao).getByName(projectName);
        Mockito.doReturn(null).when(projectsDao).getByName(projectName2);

        try {
            projectsService.swapPositionsOfProjects(projectName, projectName2);
            Assert.fail("Should throw ProjectDoesNotExistException if first project does not exist");
        } catch (ProjectDoesNotExistException ex) {
        }

        try {
            projectsService.swapPositionsOfProjects(projectName2, projectName);
            Assert.fail("Should throw ProjectDoesNotExistException if second project does not exist");
        } catch (ProjectDoesNotExistException ex) {
        }

        Mockito.doReturn(null).when(projectsDao).getByName(projectName);

        try {
            projectsService.swapPositionsOfProjects(projectName, projectName2);
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

        String projectNameToRemove = "Project2";

        Project projectToRemove = new Project();
        projectToRemove.setName(projectNameToRemove);
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

        Mockito.doReturn(projectToRemove).when(projectsDao).getByName(projectNameToRemove);

        Mockito.doReturn(projects).when(projectsDao).getAll();

        projectsService.removeProject(projectNameToRemove);

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
        String projectName = "Name";
        Project project = new Project();
        project.setName(projectName);

        Mockito.doReturn(project).when(projectsDao).getByName(projectName);

        Mockito.doReturn(new ArrayList<>()).when(projectsDao).getAll();

        projectsService.removeProject(projectName);

        Mockito.verify(projectsDao, times(0)).save(anyObject());
    }
}
