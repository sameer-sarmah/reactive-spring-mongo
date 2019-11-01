package northwind.mongo.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import northwind.documents.Shipper;

@Repository("mongoShipperRepo")
public interface ShipperRepo extends ReactiveCrudRepository<Shipper, String>{

}
