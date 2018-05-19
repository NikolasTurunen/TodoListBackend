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
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            try {
                deleteTasks(session);
                restartTasksSequence(session);

                deleteProjects(session);
                restartProjectsSequence(session);

                session.getTransaction().commit();
            } catch (Exception ex) {
                session.getTransaction().rollback();
                throw new RuntimeException("Failed to wipe the database", ex);
            }
        }
    }

    /**
     * Deletes all tasks.
     */
    private void deleteTasks(Session session) {
        Query deleteQuery = session.createNativeQuery("delete from tasks");
        deleteQuery.executeUpdate();
    }

    /**
     * Restarts the id sequence of tasks.
     */
    private void restartTasksSequence(Session session) {
        Query restartSequenceQuery = session.createNativeQuery("alter sequence tasks_id_seq restart");
        restartSequenceQuery.executeUpdate();
    }

    /**
     * Deletes all projects.
     */
    private void deleteProjects(Session session) {
        Query deleteQuery = session.createNativeQuery("delete from projects");
        deleteQuery.executeUpdate();
    }

    /**
     * Restarts the id sequence of projects.
     */
    private void restartProjectsSequence(Session session) {
        Query restartSequenceQuery = session.createNativeQuery("alter sequence projects_id_seq restart");
        restartSequenceQuery.executeUpdate();
    }
}
