Feature: Passenger Management
  As a system
  I need to manage passengers, their ratings and financial data
  To provide ride-hailing services

  Scenario: Create a new passenger with financial data
    Given a passenger with first name "Alice", last name "Smith" and email "alice@example.com"
    And financial data with balance 100.0 and card number "4111111111111111"
    When I create the passenger
    Then the passenger should be created with the provided financial data
    And the passenger's initial average rating should be 0.0

  Scenario: Update passenger rating
    Given an existing passenger with id 1 and current average rating 4.5 from 10 ratings
    When I update the passenger's rating with value 5.0
    Then the passenger's new average rating should be 4.545
    And the rating count should be 11

  Scenario: Get passenger by ID with financial data
    Given an existing passenger with id 1 and balance 50.0
    When I get the passenger by id 1
    Then I should receive the passenger's details including financial information
    And the balance should be 50.0

  Scenario: Update passenger balance successfully
    Given an existing passenger with id 1 and balance 100.0
    When I update the passenger's balance by -20.0
    Then the passenger's new balance should be 80.0

  Scenario: Fail to update balance due to insufficient funds
    Given an existing passenger with id 1 and balance 10.0
    When I try to update the passenger's balance by -20.0
    Then the operation should fail with "Insufficient balance for passenger id: 1" error

  Scenario: Update passenger information
    Given an existing passenger with id 1
    When I update the passenger's email to "new.email@example.com" and phone number to "+987654321"
    Then the passenger's information should be updated accordingly