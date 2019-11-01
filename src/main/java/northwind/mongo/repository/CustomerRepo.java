package northwind.mongo.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import northwind.documents.Customer;

@Repository("mongoCustomerRepo")
public interface CustomerRepo extends ReactiveCrudRepository<Customer, String>{

}
