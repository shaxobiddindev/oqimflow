package uz.transport.monitoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.transport.monitoring.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsernameAndActiveTrue(String username);
    boolean existsByUsername(String username);
}
