package com.assignment.product_service.bdd.steps;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.assignment.product_service.dto.ItemDTO;

import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.Given;

import java.util.List;

public class ItemSteps {

    private RestTemplate restTemplate = new RestTemplate();
    private final String BASE_URL = "http://localhost:8082/api/items";
    private ResponseEntity<String> response;
    private ResponseEntity<List<ItemDTO>> responseList;

    @Given("the item management system is ready")
    public void theItemManagementSystemIsReady() {
        
    }

    @Given("the database is empty")
    public void theDatabaseIsEmpty() {
        
    }

    @When("I add an item with:")
    public void iAddAnItemWith(io.cucumber.datatable.DataTable dataTable) {
       
        for (int i = 1; i < dataTable.asLists().size(); i++) {
            List<String> row = dataTable.asLists().get(i);
            String itemName = row.get(0);
            int qty = Integer.parseInt(row.get(1)); 

            ItemDTO itemDTO = new ItemDTO();
            itemDTO.setName(itemName);
            itemDTO.setQuantity(qty);

            response = restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                new org.springframework.http.HttpEntity<>(itemDTO),
                String.class
            );
        }
    }


    @Then("the item should be added to the inventory")
    public void theItemShouldBeAddedToTheInventory() {
        
        System.out.println("Response status code: " + response.getStatusCodeValue());
    }

    @Given("these items exist in the inventory:")
    public void theseItemsExistInTheInventory(io.cucumber.datatable.DataTable dataTable) {
        for (List<String> row : dataTable.asLists()) {
            if (row.get(0).equalsIgnoreCase("Name")) {
                continue;
            }

            String itemName = row.get(0);
            int qty;
            try {
                qty = Integer.parseInt(row.get(1)); 
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid quantity value: " + row.get(1));
            }

            ItemDTO itemDTO = new ItemDTO();
            itemDTO.setName(itemName);
            itemDTO.setQuantity(qty);

            restTemplate.exchange(
                BASE_URL,
                HttpMethod.POST,
                new org.springframework.http.HttpEntity<>(itemDTO),
                String.class
            );
        }
    }


    @When("I view all items")
    public void iViewAllItems() {
        responseList = restTemplate.exchange(
            BASE_URL,
            HttpMethod.GET,
            null,
            new org.springframework.core.ParameterizedTypeReference<List<ItemDTO>>() {}
        );
    }

    @Then("I should see:")
    public void iShouldSee(io.cucumber.datatable.DataTable dataTable) {
        List<ItemDTO> items = responseList.getBody();

        for (List<String> row : dataTable.asLists()) {
            System.out.println("Processing row: " + row);

            String expectedName = row.get(0);
            String quantityStr = row.get(1);  // The quantity should be a string

            if (quantityStr.equalsIgnoreCase("Quantity") || expectedName.equalsIgnoreCase("Name")) {
                continue;
            }

            int expectedQuantity = 0;

            try {
                expectedQuantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid quantity value: " + quantityStr);
            }

            ItemDTO item = items.stream()
                .filter(i -> i.getName().equals(expectedName))
                .findFirst()
                .orElse(null);

            if (item == null) {
                throw new AssertionError("Item not found with name: " + expectedName);
            }

            System.out.println("Item: " + expectedName + ", Expected Quantity: " + expectedQuantity + ", Actual Quantity: " + item.getQuantity());

            if (item.getQuantity() != expectedQuantity) {
                throw new AssertionError("Quantity mismatch for item: " + expectedName);
            }
        }
    }

}
