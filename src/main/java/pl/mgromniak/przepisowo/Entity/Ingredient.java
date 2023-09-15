package pl.mgromniak.przepisowo.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer quantity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "name")
    private IngredientType ingredientType;

    @ManyToOne
    @JoinColumn(name = "ingredient_fridge_id")
    private Fridge fridge;

    @ManyToOne
    @JoinColumn(name = "ingredient_recipe_id")
    private Recipe recipe;
}
