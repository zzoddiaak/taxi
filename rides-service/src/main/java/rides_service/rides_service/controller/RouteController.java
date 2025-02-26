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

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @PostMapping
    public ResponseEntity<RouteResponseDto> createRoute(@RequestBody RouteRequestDto routeRequestDto) {
        RouteResponseDto routeResponseDto = routeService.createRoute(routeRequestDto);
        return new ResponseEntity<>(routeResponseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RouteResponseDto> getRouteById(@PathVariable Long id) {
        RouteResponseDto routeResponseDto = routeService.getRouteById(id);
        return ResponseEntity.ok(routeResponseDto);
    }

    @GetMapping
    public ResponseEntity<RouteListResponseDto> getAllRoutes() {
        RouteListResponseDto routes = routeService.getAllRoutes();
        return ResponseEntity.ok(routes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RouteResponseDto> updateRoute(@PathVariable Long id, @RequestBody RouteRequestDto routeRequestDto) {
        RouteResponseDto routeResponseDto = routeService.updateRoute(id, routeRequestDto);
        return ResponseEntity.ok(routeResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }
}