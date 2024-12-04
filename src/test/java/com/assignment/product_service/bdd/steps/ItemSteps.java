package com.assignment.product_service.bdd.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import com.assignment.product_service.service.ItemService; // Import your ItemService class
import org.springframework.beans.factory.annotation.Autowired;

public class ItemSteps {

    @Autowired
    private ItemService itemService; 

    @Given("the item service is running")
    public void theItemServiceIsRunning() {
        System.out.println("Item service is running.");
    }

    @When("I add a new item with name {string} and quantity {int}")
    public void iAddANewItem(String name, int quantity) {
        
        itemService.createItem(name, quantity);
    }

    @Then("the item is successfully created")
    public void theItemIsSuccessfullyCreated() {
        System.out.println("Item successfully created.");
    }

    @When("I retrieve all items")
    public void iRetrieveAllItems() {
        System.out.println("Retrieving all items.");
    }

    @Then("I should see a list of items")
    public void iShouldSeeAListOfItems() {
        System.out.println("List of items retrieved.");
    }
}
