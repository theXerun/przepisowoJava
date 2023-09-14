package pl.mgromniak.przepisowo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDto {
    private String name;
    private String description;
    private Boolean isPublic;
    private List<IngredientTypeDto> ingredients;
}
