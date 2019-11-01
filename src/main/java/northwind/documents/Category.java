package northwind.documents;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

	private String categoryID;
	private String categoryName;
	private String description;



}
