package me.nikoltur.todolist.projects.da;

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
public class ProjectsDaoImpl implements ProjectsDao {

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public List<Project> getAll() {
        Session session = sessionFactory.getCurrentSession();

        TypedQuery<Project> query = session.createQuery("from Project p order by p.position");

        return query.getResultList();
    }

    @Override
    public Project getByName(String name) {
        Session session = sessionFactory.getCurrentSession();
        TypedQuery<Project> query = session.createQuery("from Project p where p.name = :name");
        query.setParameter("name", name);

        try {
            return query.getSingleResult();
        } catch (NoResultException ignored) {
            return null;
        }
    }

    @Override
    public Project getById(int projectId) {
        Session session = sessionFactory.getCurrentSession();
        TypedQuery<Project> query = session.createQuery("from Project p where p.id = :projectId");
        query.setParameter("projectId", projectId);

        try {
            return query.getSingleResult();
        } catch (NoResultException ignored) {
            return null;
        }
    }

    @Override
    public void save(Project project) {
        Session session = sessionFactory.getCurrentSession();
        session.save(project);
    }

    @Override
    public void remove(Project project) {
        Session session = sessionFactory.getCurrentSession();
        session.remove(project);
    }
}
