package pl.mgromniak.przepisowo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.mgromniak.przepisowo.Entity.IngredientType;

public interface IngredientTypeRepository extends JpaRepository<IngredientType, String> {
}
