package pl.mgromniak.przepisowo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.mgromniak.przepisowo.Entity.Attempt;

import java.util.Optional;

@Repository
public interface AttemptRepository extends JpaRepository<Attempt, Integer> {
    Optional<Attempt> findAttemptsByUsername(String username);
}
