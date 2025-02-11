//        IngredientList ingredientList = new IngredientList();
//        ArrayList<String > lst = ingredientList.getList();
//        System.out.println(lst);

package burgers.ingredient;

import java.util.ArrayList;
import io.restassured.response.Response;
import burgers.order.OrderApi;

public class IngredientList {
    private OrderApi orderApi;
    private Response response;
    private ArrayList<IngredientData> ingredientDataList;

    public ArrayList<String> getList() {

        orderApi = new OrderApi();
        ArrayList<String> hashList = new ArrayList<String>();
        IngredientData ingredient;

        response = orderApi.ingredients();
        InrgedientResponse inrgedientResponse = response.as(InrgedientResponse.class);

        ingredientDataList = inrgedientResponse.getData();
        Integer maxIngredient = ingredientDataList.size();
        for (int i = 0; i < ingredientDataList.size(); i++) {
            ingredient = ingredientDataList.get(i);
            hashList.add(ingredient.get_id());
            System.out.println(ingredient.get_id());
        }
        return hashList;
    }
}
