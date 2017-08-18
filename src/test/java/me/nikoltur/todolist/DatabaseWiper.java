package me.nikoltur.todolist;

import javax.persistence.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Nikolas Turunen
 */
public class DatabaseWiper {

    @Autowired
    private SessionFactory sessionFactory;

    public void wipeDatabase() {
        deleteProjects();
        restartProjectsSequence();

        deleteTasks();
        restartTasksSequence();
    }

    /**
     * Deletes all projects.
     */
    private void deleteProjects() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query deleteQuery = session.createNativeQuery("delete from projects");
            deleteQuery.executeUpdate();

            session.getTransaction().commit();
        }
    }

    /**
     * Restarts the id sequence of projects.
     */
    private void restartProjectsSequence() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query restartSequenceQuery = session.createNativeQuery("alter sequence projects_id_seq restart");
            restartSequenceQuery.executeUpdate();

            session.getTransaction().commit();
        }
    }

    /**
     * Deletes all tasks.
     */
    private void deleteTasks() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query deleteQuery = session.createNativeQuery("delete from tasks");
            deleteQuery.executeUpdate();

            session.getTransaction().commit();
        }
    }

    /**
     * Restarts the id sequence of tasks.
     */
    private void restartTasksSequence() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            Query restartSequenceQuery = session.createNativeQuery("alter sequence tasks_id_seq restart");
            restartSequenceQuery.executeUpdate();

            session.getTransaction().commit();
        }
    }
}
