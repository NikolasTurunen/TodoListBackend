package me.nikoltur.todolist.tasks.da;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * Represents a task of a project.
 *
 * @author Nikolas Turunen
 */
@Entity
@Table(name = "tasks")
public class Task implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "project_id")
    private Integer projectId;
    @Column(name = "task")
    private String taskString;
    @Column(name = "parent_task_id")
    private Integer parentTaskId;
    @OneToMany
    @JoinColumn(name = "parent_task_id", referencedColumnName = "id")
    private List<Task> details;

    public int getId() {
        return id;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public String getTaskString() {
        return taskString;
    }

    public void setTaskString(String taskString) {
        this.taskString = taskString;
    }

    public Integer getParentTaskId() {
        return parentTaskId;
    }

    public void setParentTaskId(Integer parentTaskId) {
        this.parentTaskId = parentTaskId;
    }

    public List<Task> getDetails() {
        return details;
    }
}
