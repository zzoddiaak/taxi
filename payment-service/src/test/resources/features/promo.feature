Feature: Promo Code Management
  As a marketing system
  I need to manage promo codes
  To provide discounts to customers

  Scenario: Create a new promo code
    Given a promo code request with code "WINTER10" and 10% discount
    When I create the promo code
    Then the promo code should be created with specified discount

  Scenario: Apply valid promo code discount
    Given an active promo code "SPRING15" with 15% discount
    When I apply "SPRING15" to amount 200.0
    Then the discounted amount should be 170.0

  Scenario: Get promo code by code
    Given an existing promo code "FALL25" with 25% discount
    When I get promo code by code "FALL25"
    Then I should receive the promo code details