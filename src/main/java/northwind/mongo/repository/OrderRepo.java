package northwind.mongo.repository;

import java.util.List;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import northwind.documents.Order;
import reactor.core.publisher.Flux;

@Repository("mongoOrderRepo")
public interface OrderRepo extends ReactiveCrudRepository<Order, String>{
        Flux<Order> findByOrderIDIn(List<String> orderID);
}
