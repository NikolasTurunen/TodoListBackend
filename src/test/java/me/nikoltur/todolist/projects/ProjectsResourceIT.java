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

        projectsResource.removeProject(nameOfProjectToBeRemoved);

        List<Project> projects = projectsResource.getProjects();
        Assert.assertEquals("Size should be 1", 1, projects.size());
        Assert.assertNotEquals("Name of the single project left should not be the same as of the removed project", nameOfProjectToBeRemoved, projects.get(0).getName());
    }

    @Test
    public void testRemoveProjectThrowsIfProjectDoesNotExist() {
        String projectName = "p";

        try {
            projectsResource.removeProject(projectName);
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

        projectsResource.renameProject(name, newName);

        List<Project> projects = projectsResource.getProjects();
        Assert.assertEquals("Size of projects should be 1", 1, projects.size());
        Assert.assertEquals("Name of project should be the new name", newName, projects.get(0).getName());
    }

    @Test(expected = ProjectDoesNotExistException.class)
    public void testRenameProjectThrowsForNonExistingProject() {
        projectsResource.renameProject("Name", "New name");
    }

    @Test(expected = ProjectAlreadyExistsException.class)
    public void testRenameProjectThrowsForAlreadyExistingProject() {
        String projectName = "Project";
        String newProjectName = "New project";

        projectsResource.createProject(projectName);
        projectsResource.createProject(newProjectName);

        projectsResource.renameProject(projectName, newProjectName);
    }
}
