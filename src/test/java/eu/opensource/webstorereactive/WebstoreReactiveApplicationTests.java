package eu.opensource.webstorereactive;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.hamcrest.CoreMatchers.equalTo;

import eu.opensource.webstorereactive.domain.Product;
import reactor.test.StepVerifier;

// @Disabled("For some reason these tests fail in the gradle build, run them only manually from IntelliJ IDEA")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebstoreReactiveApplicationTests {

	@Autowired
	private WebTestClient client;

	@Test
	public void getProduct() {
		client.get().uri("/products/{productCode}", "00001").exchange().expectStatus().isOk().expectHeader()
				.contentType(MediaType.APPLICATION_JSON_VALUE).expectBody(Product.class)
				.value(Product::getName, equalTo("Da Visual Basic a Java"));
	}

	@Test
	public void fetchAllBooks() {
		FluxExchangeResult<Product> result = client.get().uri("/products").exchange().expectStatus().isOk()
				.expectHeader().contentType("application/stream+json").returnResult(Product.class);

		StepVerifier.create(result.getResponseBody()).expectNextCount(3).verifyComplete();
	}
}
