Feature: Passenger Service Features
  Scenario: Passenger balance management
    Given passenger "1" has a balance of "100.00"
    When "25.00" is deducted from passenger "1"'s balance
    Then the new balance should be "75.00"

  Scenario: Passenger rating updates
    Given passenger "2" has:
      | currentRating | 4.0 |
      | ratingCount   | 3   |
    When passenger "2" receives a new rating of 5.0
    Then the new average rating should be 4.25