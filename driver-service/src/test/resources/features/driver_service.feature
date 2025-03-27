Feature: Driver Management
  As a system
  I need to manage drivers and their ratings
  To provide ride-hailing services

  Scenario: Create a new driver with a car
    Given a car with model "Toyota Camry" and plate number "ABC123"
    When I create a driver with first name "John", last name "Doe" and assign the car
    Then the driver should be created with the assigned car
    And the driver's initial average rating should be 0.0

  Scenario: Update driver rating
    Given an existing driver with id 1 and current average rating 4.0 from 5 ratings
    When I update the driver's rating with value 5.0
    Then the driver's new average rating should be 4.166
    And the rating count should be 6

  Scenario: Get driver by ID
    Given an existing driver with id 1
    When I get the driver by id 1
    Then I should receive the driver's details including car information

  Scenario: Assign a car that's already assigned to another driver
    Given a car already assigned to driver with id 2
    When I try to assign the same car to driver with id 1
    Then the operation should fail with "Car is already assigned to another driver" error

  Scenario: Update driver information
    Given an existing driver with id 1
    When I update the driver's last name to "Smith" and phone number to "+123456789"
    Then the driver's information should be updated accordingly