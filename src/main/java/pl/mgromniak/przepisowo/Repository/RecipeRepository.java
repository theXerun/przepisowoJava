package pl.mgromniak.przepisowo.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import pl.mgromniak.przepisowo.Entity.Fridge;
import pl.mgromniak.przepisowo.Entity.Ingredient;
import pl.mgromniak.przepisowo.Entity.Recipe;
import pl.mgromniak.przepisowo.Entity.User;
import pl.mgromniak.przepisowo.Exception.BreakException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
@Repository
@Transactional
public class RecipeRepository {
    @Autowired
    EntityManager em;

    public Recipe getById(Integer id) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Recipe> cq = cb.createQuery(Recipe.class);
        Root<Recipe> recipe = cq.from(Recipe.class);
        cq.where(cb.equal(recipe.get("id"), id));
        TypedQuery<Recipe> query = em.createQuery(cq);
        return query.getSingleResult();
    }

    public List<Recipe> getAvailableRecipes(User user) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Recipe> cq = cb.createQuery(Recipe.class);
        Root<Recipe> recipe = cq.from(Recipe.class);
        Predicate isPublic = cb.equal(recipe.get("isPublic"), true);
        Predicate isAuthor = cb.equal(recipe.get("author").get("id"), user.getId());
        Predicate authorOrPublic = cb.or(isAuthor, isPublic);
        cq.where(authorOrPublic);
        TypedQuery<Recipe> query = em.createQuery(cq);
        return query.getResultList();
    }

    public List<Recipe> getDoableRecipes(User user) {
        List<Recipe> available = getAvailableRecipes(user);
        List<Recipe> doable = new ArrayList<>();
        Fridge fridge = user.getFridge();

        for (Recipe recipe : available) {
            try {
                for (Ingredient recipeIngredient : recipe.getIngredients()) {
                    Optional<Ingredient> fridgeIngredient = fridge.getIngredients().stream().filter(i -> Objects.equals(i.getIngredientType().getName(), recipeIngredient.getIngredientType().getName())).findFirst();
                    if (fridgeIngredient.isEmpty() || recipeIngredient.getQuantity() > fridgeIngredient.get().getQuantity()) {
                        throw new BreakException();
                    }
                }
            } catch (BreakException e) {
                continue;
            }
            doable.add(recipe);
        }
        return doable;
    }

    public Boolean isRecipeDoable(Recipe recipe, User user) {
        try {
            for (Ingredient recipeIngredient : recipe.getIngredients()) {
                Optional<Ingredient> fridgeIngredient = user.getFridge().getIngredients().stream().filter(i -> Objects.equals(i.getIngredientType().getName(), recipeIngredient.getIngredientType().getName())).findFirst();
                if (fridgeIngredient.isEmpty() || recipeIngredient.getQuantity() > fridgeIngredient.get().getQuantity()) {
                    throw new BreakException();
                }
            }
        } catch (BreakException e) {
            return false;
        }
        return true;
    }

    public void save(Recipe recipe) {
        em.persist(recipe);
        em.flush();
    }

    public void remove(Recipe recipe) {
        em.remove(recipe);
        em.flush();
    }
}
