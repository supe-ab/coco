Feature: Item Inventory Management 
  As a manager, I want to manage items in the inventory system 
  So that I can track product information 

  Background:
    Given the item management system is ready
    And the database is empty

  Scenario Outline: Add items to inventory
    When I add an item with: 
      | Name          | Quantity |
      | <ItemName>    | <Qty>    |
    Then the item should be added to the inventory

    Examples:
      | ItemName      | Qty |
      | Wireless Mouse| 10  |
      | Keyboard      | 25  |
      | Headphones    | 50  |

  Scenario: View all items in inventory
    Given these items exist in the inventory: 
      | Name      | Quantity |
      | Laptop    | 10       |
      | Smartphone| 15       |
      | Tablet    | 5        |
    When I view all items
    Then I should see: 
      | Name      | Quantity |
      | Laptop    | 10       |
      | Smartphone| 15       |
      | Tablet    | 5        |
 	