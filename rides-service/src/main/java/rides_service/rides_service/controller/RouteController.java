package rides_service.rides_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rides_service.rides_service.dto.route.RouteListResponseDto;
import rides_service.rides_service.dto.route.RouteRequestDto;
import rides_service.rides_service.dto.route.RouteResponseDto;
import rides_service.rides_service.service.api.RouteService;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
@Tag(name = "Route Controller", description = "API for managing routes")
public class RouteController {

    private final RouteService routeService;

    @PostMapping
    @Operation(summary = "Create a route", description = "Creates a new route")
    public ResponseEntity<RouteResponseDto> createRoute(@RequestBody RouteRequestDto routeRequestDto) {
        RouteResponseDto routeResponseDto = routeService.createRoute(routeRequestDto);
        return new ResponseEntity<>(routeResponseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get route by ID", description = "Retrieves a route by its ID")
    public ResponseEntity<RouteResponseDto> getRouteById(@PathVariable Long id) {
        RouteResponseDto routeResponseDto = routeService.getRouteById(id);
        return ResponseEntity.ok(routeResponseDto);
    }

    @GetMapping
    @Operation(summary = "Get all routes", description = "Retrieves a list of all routes")
    public ResponseEntity<RouteListResponseDto> getAllRoutes() {
        RouteListResponseDto routes = routeService.getAllRoutes();
        return ResponseEntity.ok(routes);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update route", description = "Updates an existing route")
    public ResponseEntity<RouteResponseDto> updateRoute(@PathVariable Long id, @RequestBody RouteRequestDto routeRequestDto) {
        RouteResponseDto routeResponseDto = routeService.updateRoute(id, routeRequestDto);
        return ResponseEntity.ok(routeResponseDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete route", description = "Deletes a route by its ID")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }
}