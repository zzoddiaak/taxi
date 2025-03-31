Feature: Ride-Sharing Platform End-to-End Flow
  As a ride-sharing platform
  I want all services to work together seamlessly
  To provide a complete ride experience

  Background:
    Given all microservices are running
    And Kafka message broker is running
    And passenger "1" has a balance of "100.00"
    And the following passengers exist:
      | id | firstName | lastName | balance |
      | 1  | John      | Doe      | 100.00  |
      | 2  | Jane      | Smith    | 50.00   |
    And the following drivers exist:
      | id | firstName | lastName | carModel | licenseNumber |
      | 1  | Mike      | Johnson  | Tesla    | ABC123        |
      | 2  | Sarah     | Williams | Toyota   | XYZ789        |

  Scenario: Complete ride flow with rating and payment
    When passenger "1" requests a ride from "123 Main St" to "456 Oak Ave"
    Then a ride request should be created with status "PENDING"
    And a payment record should be created with status "PENDING"
    And an "available-rides" event should be published

    When driver "1" accepts the ride
    Then the ride status should be updated to "ACCEPTED"
    And a "ride-acceptance" event should be published

    When driver "1" starts the ride
    Then the ride status should be updated to "IN_PROGRESS"
    And a "ride-start" event should be published

    When driver "1" ends the ride
    Then the ride status should be updated to "COMPLETED"
    And a payment of "25.00" should be processed
    And the passenger's balance should be reduced by "25.00"
    And a "ride-completed" event should be published

    When passenger "1" rates driver "1" with score "5.0"
    Then a rating should be created in the rating service
    And driver "1"'s average rating should be updated
    And a "passenger-rating-topic" event should be published

    When driver "1" rates passenger "1" with score "4.5"
    Then a rating should be created in the rating service
    And passenger "1"'s average rating should be updated
    And a "driver-rating-topic" event should be published