//package com.assignment.product_service.bdd.steps;
//
//import io.cucumber.java.en.Given;
//import io.cucumber.java.en.Then;
//import io.cucumber.java.en.When;
//
//public class ItemSteps {
//
//    @Given("the item service is running")
//    public void theItemServiceIsRunning() {
//        // Set up your environment, mock services, or check service status
//        System.out.println("Item service is running.");
//    }
//
//    @When("I add a new item with name {string} and quantity {int}")
//    public void iAddANewItem(String name, int quantity) {
//        // Simulate adding an item
//        System.out.println("Adding item: " + name + ", quantity: " + quantity);
//    }
//
//    @Then("the item is successfully created")
//    public void theItemIsSuccessfullyCreated() {
//        // Verify item creation
//        System.out.println("Item successfully created.");
//    }
//
//    @When("I retrieve all items")
//    public void iRetrieveAllItems() {
//        // Simulate retrieval of items
//        System.out.println("Retrieving all items.");
//    }
//
//    @Then("I should see a list of items")
//    public void iShouldSeeAListOfItems() {
//        // Verify the list of items
//        System.out.println("List of items retrieved.");
//    }
//}
