import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import northwind.documents.Category;
import northwind.documents.Customer;
import northwind.documents.Order;
import northwind.documents.Product;
import northwind.mongo.aggregation.domain.AmountByOrderId;
import northwind.mongo.aggregation.domain.AmountByProductId;
import northwind.mongo.aggregation.domain.CountByProductId;
import northwind.mongo.config.ReactiveMongoConfig;
import northwind.mongo.repository.CustomerRepo;
import northwind.mongo.repository.OrderRepo;
import northwind.mongo.repository.ProductRepo;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class Driver {
	
	public static void main(String[] args) throws InterruptedException {
		ApplicationContext ctx = new AnnotationConfigApplicationContext(ReactiveMongoConfig.class);
		CustomerRepo customerRepo = (CustomerRepo) ctx.getBean(CustomerRepo.class);
		OrderRepo orderRepo = (OrderRepo) ctx.getBean( OrderRepo.class);
		Flux<Customer> customers = customerRepo.findAll()
				.sort((c1,c2)->{
					return c2.getCustomerName().compareTo(c1.getCustomerName());
				}).take(1);
		customers.subscribe((customer)->{
			System.out.println(customer.getCustomerName());
		});

		
		Mono<Order> orderMono = orderRepo.findById("10248");
		Flux<Order> orders = orderRepo.findByOrderIDIn(Arrays.asList("10248","10249","10250"));
		orders.collectList().subscribe(list -> System.out.println(list.size()));
		orderMono.subscribe((order)->{
			System.out.println("order id: "+order.getOrderID()+" shipped date is: "+order.getShippedDate());
		});
		
		
		ProductRepo productRepo = (ProductRepo) ctx.getBean(ProductRepo.class);
		productRepo.findAll()
				.groupBy(Product::getCategory)
				.subscribe((entry) -> {
					Category cat = entry.key();
					entry.collectList().subscribe((productsByCategory) -> {
						productsByCategory.forEach((product)->{
							System.out.println(cat.getCategoryID());
							System.out.println("Product is: "+product.getProductName()+" Category is  "+cat.getCategoryName());
						});
					});
				});;
		
		
		totalAmountGroupByOrder();
		mostBoughtProduct();
		mostRevenueGeneratingProduct();
		mostRevenueGeneratingOrder();

		Thread.sleep(100000);
	}
	
	
	
	/*
	 * 
	 * 
	total amount group by each order
	db.order.aggregate([
	{$unwind : "$orderitems"},
	{ $group:{_id: { OrderID : "$_id"},
	totalPrice: { $sum: { $multiply: [ "$orderitems.unitPrice", "$orderitems.quantity" ] } },}},
	{"$sort":{_id:1}},
	{"$match" :{ totalPrice : { $gte :400 }}}
	])
	
	
	alternate using addfield
	db.order.aggregate([
	{ $unwind : "$orderitems"},
	{ $addFields: { totalPrice: { $sum: { $multiply: [ "$orderitems.unitPrice", "$orderitems.quantity" ] } }}},
	{ $group:{_id: { OrderID : "$_id"},totalPrice : { $sum:"$totalPrice" }}},
	{"$sort":{_id: 1}},
	{"$match" :{ totalPrice : { $gte :400 }}}
	])
	 */
	private static void totalAmountGroupByOrder() {
		List<AggregationOperation> operations= new ArrayList<>();
		operations.add(Aggregation.unwind("orderitems"));
		operations.add(Aggregation.project("_id","orderitems.productID","orderitems.unitPrice","orderitems.quantity")
			.and("orderitems.unitPrice").multiply("orderitems.quantity").as("totalPrice"));
		operations.add(Aggregation.group("_id").sum("totalPrice").as("totalPrice"));	
		operations.add(Aggregation.sort( Sort.by(Direction.ASC, "totalPrice")));
		operations.add(Aggregation.match(new Criteria("totalPrice").gt(400)));
		Aggregation aggregations= Aggregation.newAggregation(operations);
		ApplicationContext ctx = new AnnotationConfigApplicationContext(ReactiveMongoConfig.class);
		ReactiveMongoTemplate mongoTemplate = (ReactiveMongoTemplate) ctx.getBean( ReactiveMongoTemplate.class);
	    Flux<AmountByOrderId> aggregate = mongoTemplate
	    			.aggregate(aggregations, "order", Map.class)
	    			.map((row)->{
	    				String orderID = (String) row.get("_id");
	    				Double totalPrice = (Double) row.get("totalPrice");
	    				return new AmountByOrderId(orderID,totalPrice);
	    			});
	    aggregate.subscribe((amountByOrderId)->{
	    	System.out.println("orderid is "+amountByOrderId.getOrderID()+" total price is "+ amountByOrderId.getTotalPrice());
	    });
	}
	
/*
product bought most number of times
db.order.aggregate([
{$unwind : "$orderitems"},
{ $group:{_id: { productID : "$orderitems.productID"},
 totalCount : { $sum: 1 },
}},
{"$sort":{totalCount: -1}}
])
 * 
 * */
	private static void mostBoughtProduct() {
		List<AggregationOperation> operations= new ArrayList<>();
		operations.add(Aggregation.unwind("orderitems"));
		operations.add(Aggregation.group("orderitems.productID").count().as("totalCount"));	
		operations.add(Aggregation.sort(Sort.by(Direction.DESC, "totalCount")));
		Aggregation aggregations= Aggregation.newAggregation(operations);
		ApplicationContext ctx = new AnnotationConfigApplicationContext(ReactiveMongoConfig.class);
		ReactiveMongoTemplate mongoTemplate = (ReactiveMongoTemplate) ctx.getBean( ReactiveMongoTemplate.class);
	    Flux<CountByProductId> aggregate = mongoTemplate.aggregate(aggregations, "order", Map.class)
    			.map((row)->{
    				String productID = (String) row.get("_id");
    				Integer totalCount = (Integer) row.get("totalCount");
    				return new CountByProductId(productID,totalCount);
    			});
	    aggregate.subscribe((countByProductId)->{
	    	System.out.println("product id is "+countByProductId.getProductID()+" total count is "+ countByProductId.getTotalCount());
	    });
	}
/*
	 * order which has generated most revenue

db.order.aggregate([
{$unwind : "$orderitems"},
{ $group:{_id: { OrderID : "$_id"},
totalPrice: { $sum: { $multiply: [ "$orderitems.unitPrice", "$orderitems.quantity" ] } },}},
{"$sort":{totalPrice: -1}},
{"$match" :{ totalPrice : { $gte :400 }}}
])
	 * */
	private static void mostRevenueGeneratingOrder() {
		List<AggregationOperation> operations= new ArrayList<>();
		operations.add(Aggregation.unwind("orderitems"));
		operations.add(Aggregation.project("_id","orderitems.unitPrice","orderitems.quantity")
				.and("orderitems.unitPrice").multiply("orderitems.quantity").as("totalPrice"));
		operations.add(Aggregation.group("_id").sum("totalPrice").as("totalPrice"));	
		operations.add(Aggregation.sort(Sort.by(Direction.DESC, "totalPrice")));
		Aggregation aggregations= Aggregation.newAggregation(operations);
		ApplicationContext ctx = new AnnotationConfigApplicationContext(ReactiveMongoConfig.class);
		ReactiveMongoTemplate mongoTemplate = (ReactiveMongoTemplate) ctx.getBean( ReactiveMongoTemplate.class);
	    Flux<AmountByOrderId> aggregate = mongoTemplate.aggregate(aggregations, "order", Map.class)
    			.map((row)->{
    				String OrderID = (String) row.get("_id");
    				Double totalPrice = (Double) row.get("totalPrice");
    				return new AmountByOrderId(OrderID,totalPrice);
    			});
	    aggregate.subscribe((amountByOrderId)->{
	    	System.out.println("order id is "+amountByOrderId.getOrderID()+" total price is "+ amountByOrderId.getTotalPrice());
	    });
	}
	/*
	product which has generated most revenue

	db.order.aggregate([
	{$unwind : "$orderitems"},
	{ $group:{_id: { productID : "$orderitems.productID"},
	totalPrice: { $sum: { $multiply: [ "$orderitems.unitPrice", "$orderitems.quantity" ] } },}},
	{"$sort":{totalPrice: -1}},
	{"$match" :{ totalPrice : { $gte :400 }}}
	])
	
	
	db.order.aggregate([
    {$unwind : "$orderitems"},
    {$project:{ "orderitems.productID":1, "orderitems.unitPrice":1, "orderitems.quantity":1 }},
    {$addFields: { totalPrice: { $sum:"$totalPrice"   }}},
    {$group:{_id: { productID : "$orderitems.productID"},
    totalPrice: { $sum: { $multiply: [ "$orderitems.unitPrice", "$orderitems.quantity" ] } },}},
    {"$sort":{totalPrice: -1}},
    {"$match" :{ totalPrice : { $gte :400 }}}
    ])
	*/
	
	private static void mostRevenueGeneratingProduct() {
		List<AggregationOperation> operations= new ArrayList<>();
		operations.add(Aggregation.unwind("orderitems"));
		operations.add(Aggregation.project("_id","orderitems.productID","orderitems.unitPrice","orderitems.quantity")
				.and("orderitems.unitPrice").multiply("orderitems.quantity").as("price"));
		operations.add(Aggregation.group("productID").sum("price").as("totalPrice"));
		operations.add(Aggregation.sort(Sort.by(Direction.DESC, "totalPrice")));
		Aggregation aggregations= Aggregation.newAggregation(operations);
		ApplicationContext ctx = new AnnotationConfigApplicationContext(ReactiveMongoConfig.class);
		ReactiveMongoTemplate mongoTemplate = (ReactiveMongoTemplate) ctx.getBean( ReactiveMongoTemplate.class);
	    Flux<AmountByProductId> aggregate = mongoTemplate.aggregate(aggregations, "order", Map.class)
    			.map((row)->{
    				String productID = (String) row.get("_id");
    				Double totalPrice = (Double) row.get("totalPrice");
    				return new AmountByProductId(productID,totalPrice);
    			});
	    aggregate.subscribe((priceByProductId)->{
	    	System.out.println("product id is "+priceByProductId.getProductID()+" total price is "+ priceByProductId.getTotalPrice());
	    });
	}
	
}
