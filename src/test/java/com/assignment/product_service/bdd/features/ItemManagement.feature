Feature: Item Management

  Scenario: Add a new item
    Given the item service is running
    When I add a new item with name "Laptop" and quantity "10"
    Then the item is successfully created

  Scenario: Retrieve all items
    Given the item service is running
    When I retrieve all items
    Then I should see a list of items
