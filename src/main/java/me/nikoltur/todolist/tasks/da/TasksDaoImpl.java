package me.nikoltur.todolist.tasks.da;

import java.util.List;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * Database implementation.
 *
 * @author Nikolas Turunen
 */
@Repository
public class TasksDaoImpl implements TasksDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Task> getAllOf(int projectId) {
        Session session = sessionFactory.getCurrentSession();
        TypedQuery<Task> query = session.createQuery("from Task t where t.projectId=:projectId order by t.position");
        query.setParameter("projectId", projectId);

        return query.getResultList();
    }

    @Override
    public Task getById(int taskId) {
        Session session = sessionFactory.getCurrentSession();
        TypedQuery<Task> query = session.createQuery("from Task t where t.id=:taskId");
        query.setParameter("taskId", taskId);

        try {
            return query.getSingleResult();
        } catch (NoResultException ignored) {
            return null;
        }
    }

    @Override
    public void save(Task task) {
        Session session = sessionFactory.getCurrentSession();
        session.save(task);
    }

    @Override
    public void remove(Task task) {
        Session session = sessionFactory.getCurrentSession();
        session.remove(task);
    }
}
