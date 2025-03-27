Feature: Payment Processing
  As a payment system
  I need to process ride payments
  To complete financial transactions

  Scenario: Create a new payment without promo code
    Given a payment request for ride 123 with amount 100.0 and payment method "credit_card"
    When I create the payment
    Then the payment should be created with status "pending"
    And the payment amount should be 100.0

  Scenario: Create a payment with valid promo code
    Given an active promo code "SUMMER20" with 20% discount
    And a payment request for ride 124 with amount 100.0 and promo code "SUMMER20"
    When I create the payment
    Then the payment should be created with final amount 80.0
    And the promo code should be applied

  Scenario: Update payment status for cash payment
    Given an existing cash payment with id 1 and status "pending"
    When I update the payment status to "completed"
    Then the payment status should be "completed"

  Scenario: Fail to update status for non-cash payment
    Given an existing credit card payment with id 2 and status "pending"
    When I try to update the payment status to "completed"
    Then the operation should fail with "Payment status can only be updated for cash payments" error

  Scenario: Get payment by ride ID
    Given an existing payment for ride 125
    When I get payment by ride ID 125
    Then I should receive the payment details