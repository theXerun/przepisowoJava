package pl.mgromniak.przepisowo.Controller;

import lombok.extern.java.Log;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pl.mgromniak.przepisowo.Entity.Fridge;
import pl.mgromniak.przepisowo.Entity.Ingredient;
import pl.mgromniak.przepisowo.Entity.IngredientType;
import pl.mgromniak.przepisowo.Repository.FridgeRepository;
import pl.mgromniak.przepisowo.Repository.IngredientRepository;
import pl.mgromniak.przepisowo.Repository.IngredientTypeRepository;
import pl.mgromniak.przepisowo.dto.IngredientTypeDto;
import pl.mgromniak.przepisowo.impl.CustomUserDetailsImpl;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Controller
@Log
public class FridgeController {
    @Autowired
    private FridgeRepository fridgeRepository;

    @Autowired
    private IngredientTypeRepository ingredientTypeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @GetMapping("/fridge")
    public String getFridge(@AuthenticationPrincipal CustomUserDetailsImpl userDetails, Model model) {
        model.addAttribute("ingredientTypes", ingredientTypeRepository.findAll());
        model.addAttribute("fridge", userDetails.getUser().getFridge());
        return "fridge";
    }

    @GetMapping(path = "/fridge/json", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Fridge> fridgeJson(@AuthenticationPrincipal CustomUserDetailsImpl userDetails) {
        return ResponseEntity.ok(userDetails.getUser().getFridge());
    }

    @GetMapping(path = "/fridge/add")
    public String addIngredient(@AuthenticationPrincipal CustomUserDetailsImpl userDetails, Model model) {
        model.addAttribute("ingredientTypes", ingredientTypeRepository.findAll());
        Ingredient ingredient = new Ingredient();
        ingredient.setQuantity(1);
        model.addAttribute("ingredient", ingredient);
        return "add_ingredient";
    }

    @PostMapping(path = "/fridge/add")
    public String addIngredientForm(@AuthenticationPrincipal CustomUserDetailsImpl userDetails, @ModelAttribute Ingredient ingredient, Model model) {
        Fridge fridge = userDetails.getUser().getFridge();
        if (ingredient.getIngredientType().getName() == null || ingredient.getQuantity() == null) {
            model.addAttribute("error", "Brak typu składnika");
            return "error";
        }
        ingredient.setIngredientType(ingredientTypeRepository.findById(ingredient.getIngredientType().getName()).get());
        ingredient.setFridge(fridge);
        List<Ingredient> newList = fridge.getIngredients();
        newList.add(ingredient);
        fridge.setIngredients(newList);
        fridgeRepository.save(fridge);
        return "redirect:/fridge";
    }
    @PostMapping("/fridge/edit")
    public String editIngredientForm(@AuthenticationPrincipal CustomUserDetailsImpl userDetails, @ModelAttribute Fridge fridge, Model model) {
        Fridge userFridge = userDetails.getUser().getFridge();

        List<Ingredient> formIngredients = fridge.getIngredients()
                .stream()
                .filter(i -> i.getQuantity() != 0)
                .toList();

        formIngredients = formIngredients
                .stream()
                .peek(i -> {
                    i.setIngredientType(ingredientTypeRepository
                                            .findById(i.getIngredientType().getName()).get());
                    i.setFridge(userFridge);
                })
                .toList();

        userFridge.setIngredients(formIngredients);
        fridgeRepository.save(userFridge);
        return "redirect:/fridge";
    }
}
