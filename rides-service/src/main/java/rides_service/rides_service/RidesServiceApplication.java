package rides_service.rides_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "rides_service.rides_service.service.api")
public class RidesServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RidesServiceApplication.class, args);
	}

}
