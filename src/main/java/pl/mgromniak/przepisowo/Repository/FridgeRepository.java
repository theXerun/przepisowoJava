package pl.mgromniak.przepisowo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.mgromniak.przepisowo.Entity.Fridge;

@Repository
public interface FridgeRepository extends JpaRepository<Fridge, Long> {
    Fridge findFridgeById(Integer id);
}
