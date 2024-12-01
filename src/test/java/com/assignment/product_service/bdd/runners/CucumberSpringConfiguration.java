package com.assignment.product_service.bdd.runners;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import com.assignment.product_service.ProductServiceApplication; // Your main application class

@CucumberContextConfiguration
@SpringBootTest(classes = ProductServiceApplication.class) // Specify the main Spring Boot application class
public class CucumberSpringConfiguration {
}
