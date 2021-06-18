package eu.opensource.webstorereactive.controller;

import java.time.Duration;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import eu.opensource.webstorereactive.domain.Product;
import eu.opensource.webstorereactive.repository.ProductRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@RestController
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/index")
    public Mono<String> index() {
        return Mono.just("It works");
    }

    // Test it using: curl -H "Accept:text/event-stream"
    // http://localhost:8081/product
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "/products", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Product> books() {
        Flux<Product> books = productRepository.findAll();
        Flux<Long> periodFlux = Flux.interval(Duration.ofSeconds(2)); // slowing the stream down
        return books.zipWith(periodFlux).map(Tuple2::getT1);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(value = "/products")
    public Mono<Product> save(@RequestBody Product product) {
        return productRepository.save(product);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(path = "products/{productCode}")
    public Mono<Product> show(@PathVariable String productCode) {
        return productRepository.findByProductCode(productCode);
    }
}
