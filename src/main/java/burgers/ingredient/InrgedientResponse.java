package burgers.ingredient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;

@Setter
@Getter
@AllArgsConstructor
public class InrgedientResponse {
    private Boolean success;
    private ArrayList<IngredientData> data;
}

