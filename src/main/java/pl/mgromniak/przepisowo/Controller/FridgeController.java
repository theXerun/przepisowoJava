package pl.mgromniak.przepisowo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import pl.mgromniak.przepisowo.Entity.Fridge;
import pl.mgromniak.przepisowo.Entity.Ingredient;
import pl.mgromniak.przepisowo.Entity.IngredientType;
import pl.mgromniak.przepisowo.Repository.FridgeRepository;
import pl.mgromniak.przepisowo.Repository.IngredientTypeRepository;
import pl.mgromniak.przepisowo.dto.IngredientTypeDto;
import pl.mgromniak.przepisowo.impl.CustomUserDetailsImpl;

import java.util.List;
import java.util.Optional;

@Controller
public class FridgeController {
    @Autowired
    private FridgeRepository fridgeRepository;

    @Autowired
    private IngredientTypeRepository ingredientTypeRepository;

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

    @PostMapping(path = "/fridge/edit",
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public void editIngredients(@AuthenticationPrincipal CustomUserDetailsImpl userDetails, @RequestBody List<IngredientTypeDto> ingredientList) {
        Fridge fridge = userDetails.getUser().getFridge();
        List<Ingredient> ingredients = ingredientList.stream().map(i -> {
            Optional<IngredientType> it = ingredientTypeRepository.findById(i.getName());
            Ingredient ig = new Ingredient();
            ig.setIngredientType(it.get());
            ig.setQuantity(i.getQuantity());
            return ig;
        }).toList();

        fridge.setIngredients(ingredients);
        fridgeRepository.save(fridge);
    }
}
