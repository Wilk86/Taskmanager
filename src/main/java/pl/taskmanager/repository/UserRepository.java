package pl.taskmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.taskmanager.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findFirstByEmailAndPassword(String email, String password);
    User findFirstByEmail(String email);
    User findFirstByConfirmation(String registration_confirm);
}
