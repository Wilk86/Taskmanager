package pl.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.taskmanager.model.Comment;
import pl.taskmanager.model.Task;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByTask(Task task);
}
