Feature: Rating Management
  As a system
  I need to manage ratings between drivers and passengers
  To provide feedback and improve service quality

  Scenario: Create a new rating for driver
    Given a driver with id 1 and current rating 4.0 from 10 ratings
    And a passenger with id 1 and current rating 4.5 from 8 ratings
    When I create a rating with driverId 1, passengerId 1, value 5.0 and comment "Excellent service"
    Then the rating should be created with the provided data
    And the driver's rating should be updated to 4.09
    And the passenger's rating should be updated to 4.555

  Scenario: Get rating by ID
    Given an existing rating with id 1 for driver 1 and passenger 1 with value 4.5
    When I get the rating by id 1
    Then I should receive the rating details including comment and creation timestamp

  Scenario: Update rating value
    Given an existing rating with id 1 for driver 1 with value 3.0
    And driver 1 has current rating 4.0 from 5 ratings
    When I update the rating value to 5.0
    Then the rating should be updated
    And the driver's rating should be updated to 4.33

  Scenario: Delete rating
    Given an existing rating with id 1 for driver 1 with value 4.0
    And driver 1 has current rating 4.2 from 5 ratings
    When I delete the rating with id 1
    Then the rating should be deleted
    And the driver's rating should be recalculated to 4.25