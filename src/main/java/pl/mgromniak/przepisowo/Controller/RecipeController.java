package pl.mgromniak.przepisowo.Controller;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pl.mgromniak.przepisowo.Entity.Fridge;
import pl.mgromniak.przepisowo.Entity.Ingredient;
import pl.mgromniak.przepisowo.Entity.IngredientType;
import pl.mgromniak.przepisowo.Entity.Recipe;
import pl.mgromniak.przepisowo.Repository.FridgeRepository;
import pl.mgromniak.przepisowo.Repository.IngredientRepository;
import pl.mgromniak.przepisowo.Repository.IngredientTypeRepository;
import pl.mgromniak.przepisowo.Repository.RecipeRepository;
import pl.mgromniak.przepisowo.dto.RecipeDto;
import pl.mgromniak.przepisowo.impl.CustomUserDetailsImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class RecipeController {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientTypeRepository ingredientTypeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private FridgeRepository fridgeRepository;

    @GetMapping("/recipe")
    public String allRecipes(@AuthenticationPrincipal CustomUserDetailsImpl userDetails, Model model) {
        model.addAttribute("recipes", recipeRepository.getAvailableRecipes(userDetails.getUser()));
        return "allRecipes";
    }

    @GetMapping("/recipe/{id}")
    public String getRecipe(@AuthenticationPrincipal CustomUserDetailsImpl userDetails, Model model, @PathVariable Integer id) {
        Recipe recipe = recipeRepository.getById(id);
        if (recipe == null) {
            model.addAttribute("error", "Nie ma takiego przepisu");
            return "error";
        }
        if (!(recipe.getIsPublic() || Objects.equals(recipe.getAuthor().getId(), userDetails.getUser().getId()))) {
            model.addAttribute("error", "Nie masz dostępu do tego przepisu");
            return "error";
        }
        Boolean doable = recipeRepository.isRecipeDoable(recipe, userDetails.getUser());
        model.addAttribute("isAuthor", Objects.equals(recipe.getAuthor().getUsername(), userDetails.getUser().getUsername()));
        model.addAttribute("recipe", recipe);
        model.addAttribute("doable", doable);
        return "recipe";
    }

    @GetMapping("/recipe/add")
    public String addRecipeForm(Model model) {
        model.addAttribute("ingredientTypes", ingredientTypeRepository.findAll());
        model.addAttribute("recipedto", new RecipeDto());
        return "addRecipe";
    }

    @PostMapping("/recipe/delete/{id}")
    public String removeRecipe(@AuthenticationPrincipal CustomUserDetailsImpl userDetails, @PathVariable Integer id, Model model) {
        Recipe recipe = recipeRepository.getById(id);
        if (!Objects.equals(recipe.getAuthor().getUsername(), userDetails.getUser().getUsername())) {
            model.addAttribute("error", "Nie masz uprawnień");
            return "error";
        }
        recipeRepository.remove(recipe);
        return "redirect:/recipe";
    }

    @PostMapping("/recipe/{id}/subtract")
    public String subtractIngredientsFromFridge(@AuthenticationPrincipal CustomUserDetailsImpl userDetails, @PathVariable Integer id, Model model) {
        Recipe recipe = recipeRepository.getById(id);
        if (!Objects.equals(recipe.getAuthor().getUsername(), userDetails.getUser().getUsername())) {
            model.addAttribute("error", "Nie masz uprawnień");
            return "error";
        }
        if (!recipeRepository.isRecipeDoable(recipe, userDetails.getUser())) {
            model.addAttribute("error", "Nie dasz rady tego zrobić");
            return "error";
        }
        Fridge fridge = userDetails.getUser().getFridge();
        for (Ingredient recipeIngredient : recipe.getIngredients()) {
            Optional<Ingredient> found = fridge.getIngredients().stream().filter(i -> Objects.equals(i.getIngredientType().getName(), recipeIngredient.getIngredientType().getName())).findFirst();
            if (found.isPresent()) {
                if (found.get().getQuantity() - recipeIngredient.getQuantity() > 0) {
                    found.get().setQuantity(found.get().getQuantity() - recipeIngredient.getQuantity());
                } else {
                    List<Ingredient> newIngredients = new ArrayList<>(fridge.getIngredients());
                    newIngredients.remove(found.get());
                    fridge.setIngredients(newIngredients);
                    fridgeRepository.save(fridge);
                }
            }
        }
        return "redirect:/recipe";
    }

    @PostMapping(path = "/recipe/add",
    consumes = MediaType.APPLICATION_JSON_VALUE)
    public String addRecipe(@AuthenticationPrincipal CustomUserDetailsImpl userDetails, @RequestBody RecipeDto recipeDto) {
        Recipe recipe = new Recipe();
        recipe.setAuthor(userDetails.getUser());
        recipe.setName(recipeDto.getName());
        recipe.setDescription(recipeDto.getDescription());
        recipe.setIsPublic(recipeDto.getIsPublic());
        List<Ingredient> ingredients = recipeDto.getIngredients().stream().map(i -> {
            Optional<IngredientType> it = ingredientTypeRepository.findById(i.getName());
            Ingredient ig = new Ingredient();
            ig.setIngredientType(it.get());
            ig.setQuantity(i.getQuantity());
            ig.setRecipe(recipe);
            return ig;
        }).toList();
        recipe.setIngredients(ingredients);
        recipeRepository.save(recipe);
        return "redirect:/";
    }
}
