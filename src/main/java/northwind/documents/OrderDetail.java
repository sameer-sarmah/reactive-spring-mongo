package northwind.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * We are referencing Product in OrderDetail
 * 1.Product is an independent entity
 * 2.Product is not volatile
 * 3.OrderDetail and Product are not queried together.We are interested in the total price ((1-discount)*(quantity*unitPrice))
 * 4.Product can be part of many OrderDetail
 * */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetail {
	private String productID;
	private int quantity;
	private double discount;
	private double unitPrice;
		
}
