package rides_service.rides_service.integration.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import rides_service.rides_service.dto.route.RouteRequestDto;
import rides_service.rides_service.dto.route.RouteResponseDto;
import rides_service.rides_service.dto.route.RouteListResponseDto;
import rides_service.rides_service.service.api.RouteService;

import java.util.Collections;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"available-rides", "ride-acceptance", "ride-start", "ride-end"})
@ActiveProfiles("test")
class RouteControllerRestAssuredTest {

    @LocalServerPort
    private int port;

    @MockBean
    private RouteService routeService;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void createRoute_ShouldReturnCreatedRoute() {
        RouteRequestDto routeRequestDto = new RouteRequestDto();
        routeRequestDto.setStartAddress("Start");
        routeRequestDto.setEndAddress("End");
        routeRequestDto.setDistance(10.0f);
        routeRequestDto.setEstimatedTime(30);

        RouteResponseDto routeResponseDto = new RouteResponseDto();
        routeResponseDto.setId(1L);
        routeResponseDto.setStartAddress("Start");
        routeResponseDto.setEndAddress("End");
        routeResponseDto.setDistance(10.0f);
        routeResponseDto.setEstimatedTime(30);

        when(routeService.createRoute(routeRequestDto)).thenReturn(routeResponseDto);

        given()
                .contentType(ContentType.JSON)
                .body(routeRequestDto)
                .when()
                .post("/api/routes")
                .then()
                .statusCode(201)
                .body("id", equalTo(1))
                .body("startAddress", equalTo("Start"))
                .body("endAddress", equalTo("End"))
                .body("distance", equalTo(10.0f))
                .body("estimatedTime", equalTo(30));
    }

    @Test
    void getRouteById_ShouldReturnRoute() {
        RouteResponseDto routeResponseDto = new RouteResponseDto();
        routeResponseDto.setId(1L);
        routeResponseDto.setStartAddress("Start");
        routeResponseDto.setEndAddress("End");
        routeResponseDto.setDistance(10.0f);
        routeResponseDto.setEstimatedTime(30);

        when(routeService.getRouteById(1L)).thenReturn(routeResponseDto);

        given()
                .when()
                .get("/api/routes/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("startAddress", equalTo("Start"))
                .body("endAddress", equalTo("End"))
                .body("distance", equalTo(10.0f))
                .body("estimatedTime", equalTo(30));
    }

    @Test
    void getAllRoutes_ShouldReturnListOfRoutes() {
        RouteResponseDto routeResponseDto = new RouteResponseDto();
        routeResponseDto.setId(1L);
        routeResponseDto.setStartAddress("Start");
        routeResponseDto.setEndAddress("End");
        routeResponseDto.setDistance(10.0f);
        routeResponseDto.setEstimatedTime(30);

        RouteListResponseDto routeListResponseDto = new RouteListResponseDto();
        routeListResponseDto.setRoute(Collections.singletonList(routeResponseDto));

        when(routeService.getAllRoutes()).thenReturn(routeListResponseDto);

        given()
                .when()
                .get("/api/routes")
                .then()
                .statusCode(200)
                .body("route[0].id", equalTo(1))
                .body("route[0].startAddress", equalTo("Start"))
                .body("route[0].endAddress", equalTo("End"))
                .body("route[0].distance", equalTo(10.0f))
                .body("route[0].estimatedTime", equalTo(30));
    }

    @Test
    void updateRoute_ShouldReturnUpdatedRoute() {
        RouteRequestDto routeRequestDto = new RouteRequestDto();
        routeRequestDto.setStartAddress("New Start");
        routeRequestDto.setEndAddress("New End");
        routeRequestDto.setDistance(20.0f);
        routeRequestDto.setEstimatedTime(40);

        RouteResponseDto routeResponseDto = new RouteResponseDto();
        routeResponseDto.setId(1L);
        routeResponseDto.setStartAddress("New Start");
        routeResponseDto.setEndAddress("New End");
        routeResponseDto.setDistance(20.0f);
        routeResponseDto.setEstimatedTime(40);

        when(routeService.updateRoute(1L, routeRequestDto)).thenReturn(routeResponseDto);

        given()
                .contentType(ContentType.JSON)
                .body(routeRequestDto)
                .when()
                .put("/api/routes/1")
                .then()
                .statusCode(200)
                .body("id", equalTo(1))
                .body("startAddress", equalTo("New Start"))
                .body("endAddress", equalTo("New End"))
                .body("distance", equalTo(20.0f))
                .body("estimatedTime", equalTo(40));
    }

    @Test
    void deleteRoute_ShouldReturnNoContent() {
        doNothing().when(routeService).deleteRoute(1L);

        given()
                .when()
                .delete("/api/routes/1")
                .then()
                .statusCode(204);
    }
}