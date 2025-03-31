Feature: Driver Service Features
  Scenario: Driver rating updates
    Given driver "1" has:
      | currentRating | 4.5 |
      | ratingCount   | 10  |
    When driver "1" receives a new rating of 3.0
    Then the new average rating should be approximately 4.364