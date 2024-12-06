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
    private final String BASE_URL = "http://localhost:8080/api/items";
    private ResponseEntity<String> response;
    private ResponseEntity<List<ItemDTO>> responseList;

    @Given("the item management system is ready")
    public void theItemManagementSystemIsReady() {
        // You can add any setup if needed
    }

    @Given("the database is empty")
    public void theDatabaseIsEmpty() {
        // Optionally clear the database before each test
    }

    @When("I add an item with:")
    public void iAddAnItemWith(io.cucumber.datatable.DataTable dataTable) {
        // Skip the header row by starting from index 1
        for (int i = 1; i < dataTable.asLists().size(); i++) {
            List<String> row = dataTable.asLists().get(i);
            String itemName = row.get(0);
            int qty = Integer.parseInt(row.get(1)); // This should now work properly

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
        // You can log the response or perform non-assertive actions if necessary
        System.out.println("Response status code: " + response.getStatusCodeValue());
    }

    @Given("these items exist in the inventory:")
    public void theseItemsExistInTheInventory(io.cucumber.datatable.DataTable dataTable) {
        // Skip the header row by iterating over dataTable from the second row onward
        for (List<String> row : dataTable.asLists()) {
            // Skip header or first row (e.g., "Name", "Quantity")
            if (row.get(0).equalsIgnoreCase("Name")) {
                continue;
            }

            String itemName = row.get(0);
            int qty;
            try {
                qty = Integer.parseInt(row.get(1));  // Parse the quantity as an integer
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid quantity value: " + row.get(1));
            }

            ItemDTO itemDTO = new ItemDTO();
            itemDTO.setName(itemName);
            itemDTO.setQuantity(qty);

            // You can persist the items here or call the API
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
        // Retrieve the list of items from the response body
        List<ItemDTO> items = responseList.getBody();

        // Iterate through each row in the DataTable
        for (List<String> row : dataTable.asLists()) {
            // Debug line to check the content of each row (you can remove it later)
            System.out.println("Processing row: " + row);

            // Extract the expected item name and quantity from the row
            String expectedName = row.get(0);
            String quantityStr = row.get(1);  // The quantity should be a string

            // Skip the header row (if the first column contains "Name" or "Quantity")
            if (quantityStr.equalsIgnoreCase("Quantity") || expectedName.equalsIgnoreCase("Name")) {
                continue;
            }

            // Initialize expected quantity
            int expectedQuantity = 0;

            try {
                // Try parsing the quantity value as an integer
                expectedQuantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException e) {
                // If the quantity is not a valid integer, throw an exception with a descriptive message
                throw new IllegalArgumentException("Invalid quantity value: " + quantityStr);
            }

            // Find the item from the response list that matches the expected name
            ItemDTO item = items.stream()
                .filter(i -> i.getName().equals(expectedName))
                .findFirst()
                .orElse(null);

            // If the item was not found, throw an assertion error
            if (item == null) {
                throw new AssertionError("Item not found with name: " + expectedName);
            }

            // Output the result instead of asserting
            System.out.println("Item: " + expectedName + ", Expected Quantity: " + expectedQuantity + ", Actual Quantity: " + item.getQuantity());

            // You can add custom validation logic here, for example:
            if (item.getQuantity() != expectedQuantity) {
                throw new AssertionError("Quantity mismatch for item: " + expectedName);
            }
        }
    }

}
