package com.assignment.product_service.performance;



import static io.gatling.javaapi.core.CoreDsl.StringBody;
import static io.gatling.javaapi.core.CoreDsl.atOnceUsers;
import static io.gatling.javaapi.core.CoreDsl.rampUsers;
import static io.gatling.javaapi.core.CoreDsl.scenario;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import io.gatling.javaapi.http.HttpRequestActionBuilder;

public class ProductServiceSimulation extends Simulation {

    // HTTP Protocol Configuration
    HttpProtocolBuilder httpProtocol = http
        .baseUrl("http://localhost:8082")
        .acceptHeader("application/json")
        .contentTypeHeader("application/json");

    // JSON Payload for the GET request (if needed)
    // Though GET APIs typically do not have a body, you can customize headers or query parameters as needed

    // Define the GET request for retrieving items
    HttpRequestActionBuilder getItemsRequest = http("Get Items")
        .get("/api/items")
        .check(status().is(200)); // Check that the status code is 200

    // Define the scenario: Send requests to get items
    ScenarioBuilder scn = scenario("Items Retrieval Load Test")
        .exec(getItemsRequest);

    {
        setUp(
            scn.injectOpen(
                atOnceUsers(50),          // Inject 5 users at once (concurrent requests)
                rampUsers(100).during(10) // Ramp up to 10 users over 10 seconds
            ).protocols(httpProtocol)
        );
    }
}