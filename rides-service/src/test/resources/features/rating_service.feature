Feature: Rating Service Features
  Scenario: Calculate average ratings correctly
    Given driver "2" has:
      | currentRating | 4.0 |
      | ratingCount   | 5   |
    When driver "2" receives a new rating of 5.0
    Then the new average rating should be approximately 4.091

  Scenario: Rating with invalid user
    When attempting to create a rating for non-existent passenger "999"
    Then the request should fail with "404 Not Found"