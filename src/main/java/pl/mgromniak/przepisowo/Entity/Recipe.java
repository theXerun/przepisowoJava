package pl.mgromniak.przepisowo.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;
    private Boolean isPublic;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "ingredient_id")
    private List<Ingredient> ingredients;


}
