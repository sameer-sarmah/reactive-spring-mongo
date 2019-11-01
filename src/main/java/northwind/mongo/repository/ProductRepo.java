package northwind.mongo.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import northwind.documents.Product;

@Repository("mongoProductRepo")
public interface ProductRepo extends ReactiveCrudRepository<Product, String>{

}
