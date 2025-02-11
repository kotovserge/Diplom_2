package burgers.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;
import java.util.ArrayList;

@Setter
@Getter
@AllArgsConstructor
public class Order {
    private Boolean success;
    private ArrayList<OrderData> data;
}
