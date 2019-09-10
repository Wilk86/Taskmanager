package pl.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.taskmanager.model.Project;
import pl.taskmanager.model.Task;
import pl.taskmanager.model.enums.TaskStatus;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task,Long> {
    Integer countAllByTaskStatus(TaskStatus taskStatus);
    List<Task> findAllByProject(Project project);
}
