Feature: Ride Service Features
  Scenario: Calculate ride cost correctly
    Given a route with distance "5.5" km
    When calculating the ride cost
    Then the cost should be "55.00" at rate "10.00" per km

  Scenario: Ride cancellation
    When passenger "1" cancels an existing ride
    Then the ride status should be "CANCELLED"
    And any pending payment should be voided