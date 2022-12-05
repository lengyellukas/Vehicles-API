package com.udacity.pricing;

import com.udacity.pricing.domain.price.Price;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PricingServiceApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	public void contextLoads() {
	}

	@Test
	public void getPriceValidVehicle() throws Exception {
		ResponseEntity<Price> response =
				this.restTemplate.getForEntity("http://localhost:"
						+ port + "/prices/1", Price.class);

		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
		assertThat(response.getHeaders().getContentType(), equalTo(MediaType.APPLICATION_JSON_UTF8));

		Price price = response.getBody();
		assertThat(price.getCurrency(), equalTo("USD"));
		assertThat(price.getPrice().compareTo(BigDecimal.ZERO), equalTo(1));
	}

	@Test
	public void getPriceInvalidVehicle() throws Exception {
		ResponseEntity<Price> response =
				this.restTemplate.getForEntity("http://localhost:"
						+ port + "/prices/21", Price.class);

		assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
	}

}
