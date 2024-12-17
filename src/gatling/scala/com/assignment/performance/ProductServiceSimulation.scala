package com.assignment.performance

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class ProductServiceSimulation extends Simulation {

  // HTTP Protocol Configuration
  val httpProtocol = http
    .baseUrl("http://localhost:8082") // Base URL for the requests
    .acceptHeader("application/json") // Accept header
    .contentTypeHeader("application/json") // Content-type header for POST requests

  // JSON Payload for POST request
  val createProductPayload = """{
    "name": "Test Product",
    "quantity" : 10
  }"""

  // Scenario: Create Product Load Test
  val createProductScenario = scenario("Create Product Load Test")
    .exec(
      http("Create Product")
        .post("/api/items")
        .body(StringBody(createProductPayload)) // Send the JSON payload
        .asJson
        .check(status.is(200)) // Check that the status code is 200 (Created)
    )

  // Load Simulation Configuration
  setUp(
    createProductScenario.inject(
      // Inject users into the simulation
      atOnceUsers(10), // Inject 10 users immediately
      rampUsers(10).during(10.seconds), // Ramp up to 10 users over 10 seconds
      constantUsersPerSec(2).during(30.seconds) // Constant 2 users per second for 30 seconds
    ).protocols(httpProtocol)
  )

  // Optional: Throttle configuration
  .throttle(
    // Limit to 10 requests per second
    reachRps(10).in(10.seconds),
    // Hold 10 requests per second for 30 seconds
    holdFor(30.seconds),
    // Drop to 5 requests per second
    jumpToRps(5),
    // Hold for 10 seconds
    holdFor(10.seconds)
  )
}
