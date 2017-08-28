package me.nikoltur.todolist.projects;

import java.util.List;
import me.nikoltur.todolist.Application;
import me.nikoltur.todolist.DatabaseWiper;
import me.nikoltur.todolist.projects.da.Project;
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
@ContextConfiguration(classes = {Application.class})
public class ProjectsResourceIT {

    @Autowired
    private DatabaseWiper databaseWiper;
    @Autowired
    private ProjectsResource projectsResource;

    @Before
    public void setUp() {
        databaseWiper.wipeDatabase();
    }

    @Test
    public void testNoProjectsInitially() {
        Assert.assertTrue("Should be an empty list of projects", projectsResource.getProjects().isEmpty());
    }

    @Test
    public void testCreateAndGet() {
        String projectName = "Test";
        projectsResource.createProject(projectName);

        List<Project> projects = projectsResource.getProjects();

        Assert.assertEquals("Size should be 1", 1, projects.size());
        Assert.assertEquals("Name of first project should match", projectName, projects.get(0).getName());
        Assert.assertEquals("Id of first project should match", 1, projects.get(0).getId());
    }

    @Test
    public void testCreateThrowsIfProjectAlreadyExists() {
        String projectName = "Testitall";
        projectsResource.createProject(projectName);
        try {
            projectsResource.createProject(projectName);
            Assert.fail();
        } catch (ProjectAlreadyExistsException ex) {
            Assert.assertSame("Should throw ProjectAlreadyExistsException if a project with the same name already exists", ProjectAlreadyExistsException.class, ex.getClass());
        }
    }

    @Test
    public void testRemoveProject() {
        String nameOfProjectToBeRemoved = "p2";
        projectsResource.createProject("p1");
        projectsResource.createProject(nameOfProjectToBeRemoved);

        List<Project> projects = projectsResource.getProjects();
        for (Project project : projects) {
            if (project.getName().equals(nameOfProjectToBeRemoved)) {
                projectsResource.removeProject(project.getId());
            }
        }

        List<Project> projectsAfterRemoval = projectsResource.getProjects();
        Assert.assertEquals("Size should be 1", 1, projectsAfterRemoval.size());
        Assert.assertNotEquals("Name of the single project left should not be the same as of the removed project", nameOfProjectToBeRemoved, projectsAfterRemoval.get(0).getName());
    }

    @Test
    public void testRemoveProjectThrowsIfProjectDoesNotExist() {
        int projectId = 1;

        try {
            projectsResource.removeProject(projectId);
            Assert.fail();
        } catch (ProjectDoesNotExistException ex) {
            Assert.assertSame("Should throw ProjectDoesNotExistException if a project with the specified name does not exist", ProjectDoesNotExistException.class, ex.getClass());
        }
    }

    @Test
    public void testRenameProject() {
        String name = "Project";
        String newName = "New Project Name";

        projectsResource.createProject(name);
        Project project = projectsResource.getProjects().get(0);

        projectsResource.renameProject(project.getId(), newName);

        List<Project> projects = projectsResource.getProjects();
        Assert.assertEquals("Size of projects should be 1", 1, projects.size());
        Assert.assertEquals("Name of project should be the new name", newName, projects.get(0).getName());
    }

    @Test(expected = ProjectDoesNotExistException.class)
    public void testRenameProjectThrowsForNonExistingProject() {
        projectsResource.renameProject(1, "New name");
    }

    @Test(expected = ProjectAlreadyExistsException.class)
    public void testRenameProjectThrowsForAlreadyExistingProject() {
        String projectName = "Project";
        String newProjectName = "New project";

        projectsResource.createProject(projectName);
        projectsResource.createProject(newProjectName);
        for (Project project : projectsResource.getProjects()) {
            if (project.getName().equals(projectName)) {
                projectsResource.renameProject(project.getId(), newProjectName);
            }
        }
    }

    @Test
    public void testCreateProjectSavesPosition() {
        String firstProjectName = "Name1";
        String secondProjectName = "Name2";
        projectsResource.createProject(firstProjectName);
        projectsResource.createProject(secondProjectName);
        List<Project> projects = projectsResource.getProjects();
        Assert.assertEquals("Position of first project should be 0", 0, projects.get(0).getPosition());
        Assert.assertEquals("Position of second project should be 1", 1, projects.get(1).getPosition());
    }

    @Test
    public void testSwapPositionsOfProjects() {
        String firstProjectName = "Name1";
        String secondProjectName = "Name2";
        Project project1 = createProject(firstProjectName);
        Project project2 = createProject(secondProjectName);
        projectsResource.swapPositionsOfProjects(project1.getId(), project2.getId());
        List<Project> projects = projectsResource.getProjects();
        for (Project project : projects) {
            if (project.getName().equals(firstProjectName)) {
                Assert.assertEquals("Position of first project should now be 1", 1, project.getPosition());
            } else if (project.getName().equals(secondProjectName)) {
                Assert.assertEquals("Position of second project should now be 0", 0, project.getPosition());
            } else {
                Assert.fail("Should not return an unexpected project");
            }
        }
    }

    @Test
    public void testGetProjectsOrdersProjectsByPosition() {
        String firstProjectName = "Name1";
        String secondProjectName = "Name2";
        Project project1 = createProject(firstProjectName);
        Project project2 = createProject(secondProjectName);
        projectsResource.swapPositionsOfProjects(project1.getId(), project2.getId());
        List<Project> projects = projectsResource.getProjects();
        Assert.assertEquals("Name of first project in the list should be the name of the second project after swap", secondProjectName, projects.get(0).getName());
        Assert.assertEquals("Position of first project in the list should be 0 after swap", 0, projects.get(0).getPosition());
        Assert.assertEquals("Name of second project in the list should be the name of the first project after swap", firstProjectName, projects.get(1).getName());
        Assert.assertEquals("Position of second project in the list should be 1 after swap", 1, projects.get(1).getPosition());
    }

    @Test
    public void testRemoveProjectUpdatesPositions() {
        String firstProjectName = "Name1";
        String secondProjectName = "Name2";
        String thirdProjectName = "Name3";
        projectsResource.createProject(firstProjectName);
        projectsResource.createProject(secondProjectName);
        projectsResource.createProject(thirdProjectName);

        for (Project project : projectsResource.getProjects()) {
            if (project.getName().equals(firstProjectName)) {
                projectsResource.removeProject(project.getId());
            }
        }

        List<Project> projects = projectsResource.getProjects();
        Assert.assertEquals("Position of the first project of the returned list should be 0", 0, projects.get(0).getPosition());
        Assert.assertEquals("Position of the second project of the returned list should be 1", 1, projects.get(1).getPosition());
    }

    /**
     * Creates a project with the specified name and returns it.
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
}
