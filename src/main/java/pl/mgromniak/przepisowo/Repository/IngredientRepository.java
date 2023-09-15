package pl.mgromniak.przepisowo.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.mgromniak.przepisowo.Entity.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
}
