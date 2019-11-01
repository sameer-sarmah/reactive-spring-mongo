package northwind.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * We are embedding Category in Product
 * 1.Our use case is that category will not be queried independently thus need not be a independent entity,that is category is a dependent entity
 * 2.Category is not volatile
 * 3.When products are queried then their category is also desired,that is both are queried together
 * 4.Our use case is that one product can belong to only one category 
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Product {
	@Id
	private String productID;
	private String productName;
	private Category category;
	private String quantityPerUnit;
	private double unitPrice;
	
}
