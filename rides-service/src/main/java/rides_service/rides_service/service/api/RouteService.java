package rides_service.rides_service.service.api;

import rides_service.rides_service.dto.route.RouteRequestDto;
import rides_service.rides_service.dto.route.RouteResponseDto;

import java.util.List;

public interface RouteService {
    RouteResponseDto createRoute(RouteRequestDto routeRequestDto);
    RouteResponseDto getRouteById(Long id);
    List<RouteResponseDto> getAllRoutes();
    RouteResponseDto updateRoute(Long id, RouteRequestDto routeRequestDto);
    void deleteRoute(Long id);
}
