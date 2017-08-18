package me.nikoltur.todolist.tasks.da;

import java.util.List;
import javax.persistence.TypedQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Database implementation.
 *
 * @author Nikolas Turunen
 */
public class TasksDaoImpl implements TasksDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Task> getAllOf(int projectId) {
        Session session = sessionFactory.getCurrentSession();
        TypedQuery<Task> query = session.createQuery("from Task t where t.projectId=:projectId");
        query.setParameter("projectId", projectId);

        return query.getResultList();
    }

    @Override
    public void save(Task task) {
        Session session = sessionFactory.getCurrentSession();
        session.save(task);
    }
}
