package northwind.mongo.aggregation.domain;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AmountByProductId {
  	@Id
    private String productID;
    private Double totalPrice;
      
}
