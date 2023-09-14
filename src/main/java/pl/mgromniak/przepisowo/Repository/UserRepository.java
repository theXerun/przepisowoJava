package pl.mgromniak.przepisowo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.mgromniak.przepisowo.Entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findUserByUsername(String username);
    boolean existsByUsername(String username);
}
