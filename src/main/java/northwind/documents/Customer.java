package northwind.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * We are embedding Address in Customer
 * 1.Our use case is that Address will not be queried independently thus need not be a independent entity,that is Address is a dependent entity
 * 2.Address is not volatile
 * 3.Customer and his Address, both are queried together
 * 4.Our use case is that one customer can have only one address 
 * */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Customer {

	@Id
	private String customerID;
	private String customerName;
	private String phone;

	private Address address;

}
