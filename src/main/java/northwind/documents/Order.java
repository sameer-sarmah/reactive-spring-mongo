package northwind.documents;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Order {
	
	@Id
	String orderID;
	Date orderedDate;
	Date shippedDate;
	Address shippedAddress;
	Shipper shipper;
	Customer customer;
	List<OrderDetail> orderitems;
	
}
