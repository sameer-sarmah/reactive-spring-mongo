package northwind.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

	private String street;
	private String city;
	private String country;
	private long ZIP;

}
