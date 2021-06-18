package eu.opensource.webstorereactive.service;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.stereotype.Component;

import eu.opensource.webstorereactive.domain.Product;
import eu.opensource.webstorereactive.repository.ProductRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class DataInitializer {

    public static class Category {
        public static final String SPRING = "Spring";
        public static final String JAVA = "Java";
        public static final String WEB = "Web";
    }

    @Autowired
    ReactiveMongoOperations operations;

    private final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final ProductRepository productRepository;

    public DataInitializer(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        operations.collectionExists(Product.class)
                .flatMap(exists -> exists ? operations.dropCollection(Product.class) : Mono.just(exists))
                .then(operations.createCollection(Product.class, CollectionOptions.empty()))
                .subscribe(data -> logger.info("Collection saved: {}", data), 
                           error -> logger.info("Opps!"), 
                           () -> logger.info("Collection initialized!"));

        logger.info(" -->> Starting collection initialization...");

        productRepository
                .saveAll(Flux.just(new Product("Da Visual Basic a Java", "00001", new BigDecimal("19.90"), Category.JAVA),
                                   new Product("Java Web Service", "00002", new BigDecimal("24.90"), Category.WEB),
                                   new Product("Spring in Motion", "00003", new BigDecimal("35.00"), Category.SPRING))
                        ).subscribe(data -> logger.info("Saved {} products", data), 
                                    error -> logger.error("Oops!"),
                                    () -> logger.info("Collection initialized!"));
    }
}
