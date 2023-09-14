package pl.mgromniak.przepisowo.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import pl.mgromniak.przepisowo.Repository.RecipeRepository;
import pl.mgromniak.przepisowo.Service.CustomUserDetailsService;
import pl.mgromniak.przepisowo.Service.UserService;
import pl.mgromniak.przepisowo.impl.CustomUserDetailsImpl;

@Controller
public class HomepageController {

    @Autowired
    private RecipeRepository recipeRepository;

    @GetMapping("/")
    public String index(@AuthenticationPrincipal CustomUserDetailsImpl userDetails, Model model) {
        model.addAttribute("username", userDetails.getUsername());
        model.addAttribute("recipes", recipeRepository.getDoableRecipes(userDetails.getUser()));
        return "index";
    }
}
