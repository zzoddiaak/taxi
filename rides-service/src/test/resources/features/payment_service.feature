Feature: Payment Service Features
  Scenario: Apply promo code discount
    Given a promo code "SUMMER20" with 20% discount exists
    When passenger "2" requests a ride with promo code "SUMMER20" for a "10.00" ride
    Then the final payment amount should be "8.00"

  Scenario: Failed payment processing
    Given passenger "2" has a balance of "5.00"
    When passenger "2" requests a ride costing "10.00"
    Then the payment should be rejected with "INSUFFICIENT_FUNDS" status