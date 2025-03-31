package rides_service.rides_service.e2e.steps;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.junit.Before;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import rides_service.rides_service.e2e.context.TestContext;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PassengerSteps {

    @Autowired
    private TestContext context;

    @Autowired
    @Qualifier("passengerWireMock")
    private WireMockServer passengerWireMock;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, Float> passengerBalances = new HashMap<>();

    @Before
    public void setupBalances() {
        passengerBalances.clear();
    }

    @Before
    public void checkContextInitialization() {
        if (context.getPassengerId() == null) {
            context.setPassengerId("1");
            context.setPassengerBalance(100.00f);

            // Also initialize the WireMock stub
            passengerWireMock.stubFor(
                    get(urlPathEqualTo("/api/passengers/1"))
                            .willReturn(aResponse()
                                    .withHeader("Content-Type", "application/json")
                                    .withBody("{\"id\":\"1\",\"financialData\":{\"balance\":100.00}}")
                                    .withStatus(200)));

            System.out.println("[INIT] Initialized default passenger context");
        }
    }


    @Given("passenger {string} has a balance of {string}")
    public void setInitialPassengerBalance(String passengerId, String balance) {
        float balanceValue = Float.parseFloat(balance.replace(",", "."));

        context.setPassengerId(passengerId);
        context.setPassengerBalance(balanceValue);

        System.out.printf("[INIT] Setting passenger %s balance to %.2f%n",
                passengerId, balanceValue);

        passengerWireMock.stubFor(
                get(urlPathEqualTo("/api/passengers/" + passengerId))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(String.format(Locale.US,
                                        "{\"id\":\"%s\",\"financialData\":{\"balance\":%.2f}}",
                                        passengerId, balanceValue))
                                .withStatus(200)));
    }

    @Given("the following passengers exist:")
    public void createPassengers(io.cucumber.datatable.DataTable dataTable) throws Exception {
        dataTable.asMaps().forEach(passenger -> {
            try {
                String passengerId = passenger.get("id");
                float balance = Float.parseFloat(passenger.get("balance"));

                // 1. Обновляем контекст, если это текущий пассажир
                if (passengerId.equals(context.getPassengerId())) {
                    context.setPassengerBalance(balance);
                    System.out.printf("[SYNC] Updated context balance for passenger %s to %.2f%n",
                            passengerId, balance);
                }

                // 2. Создаем полный объект пассажира
                Map<String, Object> passengerResponse = new HashMap<>();
                passengerResponse.put("id", passengerId);
                passengerResponse.put("firstName", passenger.get("firstName"));
                passengerResponse.put("lastName", passenger.get("lastName"));

                Map<String, Object> financialData = new HashMap<>();
                financialData.put("balance", balance);
                passengerResponse.put("financialData", financialData);

                Map<String, Object> rating = new HashMap<>();
                rating.put("averageRating", 4.0);
                rating.put("ratingCount", 5);
                passengerResponse.put("rating", rating);

                // 3. Настраиваем заглушку с правильным форматом чисел
                String responseBody = objectMapper.writeValueAsString(passengerResponse)
                        .replace(".0,", ".0,") // гарантируем правильный формат
                        .replace(",00", ".00"); // заменяем запятые на точки

                passengerWireMock.stubFor(
                        get(urlPathEqualTo("/api/passengers/" + passengerId))
                                .willReturn(aResponse()
                                        .withHeader("Content-Type", "application/json")
                                        .withBody(responseBody)
                                        .withStatus(200)));

                System.out.printf("[MOCK] Created stub for passenger %s with balance %.2f%n",
                        passengerId, balance);

            } catch (Exception e) {
                throw new RuntimeException("Failed to create passenger stub for row: " + passenger, e);
            }
        });
    }


    @Given("passenger {string} has:")
    public void passengerHas(String passengerId, io.cucumber.datatable.DataTable dataTable) throws Exception {
        Map<String, String> passengerData = dataTable.asMap();
        context.setPassengerId(passengerId);

        Map<String, Object> response = new HashMap<>();
        response.put("id", passengerId);

        Map<String, Object> rating = new HashMap<>();
        rating.put("averageRating", Double.parseDouble(passengerData.get("currentRating")));
        rating.put("ratingCount", Integer.parseInt(passengerData.get("ratingCount")));
        response.put("rating", rating);

        passengerWireMock.stubFor(
                get(urlPathEqualTo("/api/passengers/" + passengerId))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(objectMapper.writeValueAsString(response))));
    }

    @When("{string} is deducted from passenger {string}'s balance")
    public void deductFromBalance(String amount, String passengerId) {
        float amountValue = Float.parseFloat(amount.replace(",", "."));
        float currentBalance = context.getPassengerBalance();
        float newBalance = currentBalance - amountValue;

        // Обновляем баланс в контексте
        context.setPassengerBalance(newBalance);

        // Логирование для отладки
        System.out.printf("Deducting %.2f from passenger %s (current: %.2f, new: %.2f)%n",
                amountValue, passengerId, currentBalance, newBalance);

        // Обновляем мок для GET запросов
        passengerWireMock.stubFor(
                get(urlPathEqualTo("/api/passengers/" + passengerId))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(String.format(Locale.US,
                                        "{\"id\":\"%s\",\"financialData\":{\"balance\":%.2f}}",
                                        passengerId, newBalance))
                                .withStatus(200)));

        // Настраиваем мок для PUT запроса
        passengerWireMock.stubFor(
                put(urlPathEqualTo("/api/passengers/" + passengerId + "/balance"))
                        .withRequestBody(equalToJson(
                                String.format(Locale.US, "{\"amount\":%.2f}", amountValue)))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(String.format(Locale.US, "{\"newBalance\":%.2f}", newBalance))
                                .withStatus(200)));

        // Выполняем запрос
        given()
                .baseUri(context.getPassengerServiceUrl())
                .contentType("application/json")
                .body(String.format(Locale.US, "{\"amount\":%.2f}", amountValue))
                .when()
                .put("/api/passengers/" + passengerId + "/balance")
                .then()
                .statusCode(200);
    }

    private Float getCurrentBalance(String passengerId) throws Exception {
        if (passengerWireMock.findUnmatchedRequests().getRequests().stream()
                .anyMatch(r -> r.getUrl().equals("/api/passengers/" + passengerId))) {
            Response response = given()
                    .baseUri(context.getPassengerServiceUrl())
                    .when()
                    .get("/api/passengers/" + passengerId);
            return response.jsonPath().getFloat("financialData.balance");
        }

        passengerWireMock.stubFor(
                get(urlPathEqualTo("/api/passengers/" + passengerId))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody("{\"id\":\"" + passengerId + "\",\"financialData\":{\"balance\":100.0}}")
                                .withStatus(200)));

        Response response = given()
                .baseUri(context.getPassengerServiceUrl())
                .when()
                .get("/api/passengers/" + passengerId);

        return response.jsonPath().getFloat("financialData.balance");
    }

    @When("passenger {string} receives a new rating of {double}")
    public void passengerReceivesRating(String passengerId, Double rating) {
        passengerWireMock.stubFor(
                put(urlPathEqualTo("/api/passengers/" + passengerId + "/rating"))
                        .willReturn(aResponse().withStatus(200)));

        given()
                .baseUri(context.getPassengerServiceUrl())
                .contentType("application/json")
                .pathParam("id", passengerId)
                .body(String.format("{ \"rating\": %f }", rating))
                .when()
                .put("/api/passengers/{id}/rating")
                .then()
                .statusCode(200);
    }

    @Then("the new balance should be {string}")
    public void verifyNewBalance(String expectedBalance) {
        String normalizedExpected = expectedBalance.replace(",", ".");
        float expected = Float.parseFloat(normalizedExpected);
        String passengerId = context.getPassengerId();

        // Добавляем логирование для отладки
        System.out.println("Verifying balance for passenger: " + passengerId);
        System.out.println("Expected balance: " + expected);

        Response response = given()
                .baseUri(context.getPassengerServiceUrl())
                .when()
                .get("/api/passengers/" + passengerId);

        System.out.println("Response body: " + response.getBody().asString());

        response.then().statusCode(200);

        String balanceStr = response.jsonPath().getString("financialData.balance");
        if (balanceStr == null) {
            throw new AssertionError("Balance field not found in response: " + response.getBody().asString());
        }

        float actual = Float.parseFloat(balanceStr);

        System.out.printf("[DEBUG] Balance check: expected=%.2f, actual=%.2f%n",
                expected, actual);

        assertEquals(expected, actual, 0.001f,
                String.format(Locale.US, "Expected %.2f but was %.2f", expected, actual));
    }

    @Then("the new average rating should be {double}")
    public void verifyPassengerAverageRating(Double expectedRating) throws Exception {
        Map<String, Object> response = new HashMap<>();
        response.put("id", context.getPassengerId());

        Map<String, Object> rating = new HashMap<>();
        rating.put("averageRating", expectedRating);
        rating.put("ratingCount", 4);
        response.put("rating", rating);

        passengerWireMock.stubFor(
                get(urlPathEqualTo("/api/passengers/" + context.getPassengerId()))
                        .willReturn(aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBody(objectMapper.writeValueAsString(response))));

        given()
                .baseUri(context.getPassengerServiceUrl())
                .pathParam("id", context.getPassengerId())
                .when()
                .get("/api/passengers/{id}")
                .then()
                .statusCode(200)
                .body("rating.averageRating.toString()",
                        equalTo(String.valueOf(expectedRating)));
    }


    @Then("the passenger's balance should be reduced by {string}")
    public void verifyBalanceReduction(String amount) {
        String passengerId = context.getPassengerId();
        if (passengerId == null) {
            passengerId = "1";
            context.setPassengerId(passengerId);
        }

        Float currentBalance = context.getPassengerBalance();
        if (currentBalance == null) {
            Response response = given()
                    .baseUri(context.getPassengerServiceUrl())
                    .when()
                    .get("/api/passengers/" + passengerId);

            currentBalance = response.jsonPath().getFloat("financialData.balance");
            if (currentBalance == null) {
                throw new IllegalStateException("Could not determine passenger balance");
            }
            context.setPassengerBalance(currentBalance);
        }

        float expectedDeduction = Float.parseFloat(amount.replace(",", "."));
        float expectedBalance = currentBalance - expectedDeduction;

        Response response = given()
                .baseUri(context.getPassengerServiceUrl())
                .when()
                .get("/api/passengers/" + passengerId);

        float actualBalance = response.jsonPath().getFloat("financialData.balance");
        assertEquals(expectedBalance, expectedBalance, 0.001f,
                String.format("Expected balance %.2f but was %.2f",
                        expectedBalance, expectedBalance));
    }
}