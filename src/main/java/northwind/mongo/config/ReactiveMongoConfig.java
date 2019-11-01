package northwind.mongo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

@Configuration
@ComponentScan("northwind")
@EnableReactiveMongoRepositories(basePackages = { "northwind.mongo.repository" })
public class ReactiveMongoConfig extends AbstractReactiveMongoConfiguration{
  
	@Bean
	@Override
	public MongoClient reactiveMongoClient() {
		//default is "mongodb://localhost"
		return MongoClients.create();
	}


	@Override
	protected String getDatabaseName() {
		return "northwind";
	}
	

    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        return new ReactiveMongoTemplate(reactiveMongoClient(), getDatabaseName());
    }
}
