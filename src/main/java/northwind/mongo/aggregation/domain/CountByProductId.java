package northwind.mongo.aggregation.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountByProductId {

	private String productID;
	private int totalCount;

}
