package rides_service.rides_service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rides_service.rides_service.config.mapper.DtoMapper;
import rides_service.rides_service.dto.route.RouteListResponseDto;
import rides_service.rides_service.dto.route.RouteRequestDto;
import rides_service.rides_service.dto.route.RouteResponseDto;
import rides_service.rides_service.entity.Route;
import rides_service.rides_service.exception.route.RouteNotFoundException;
import rides_service.rides_service.repository.RouteRepository;
import rides_service.rides_service.service.api.RouteService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final DtoMapper mapper;

    @Override
    public RouteResponseDto createRoute(RouteRequestDto routeRequestDto) {
        log.info("Creating new route: {}", routeRequestDto);
        Route route = mapper.convertToEntity(routeRequestDto, Route.class);
        Route savedRoute = routeRepository.save(route);
        log.debug("Route created successfully: {}", savedRoute);
        return mapper.convertToDto(savedRoute, RouteResponseDto.class);
    }

    @Override
    public RouteResponseDto getRouteById(Long id) {
        log.debug("Fetching route by id: {}", id);
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Route not found: id={}", id);
                    return new RouteNotFoundException("Route not found with id: " + id);
                });
        log.info("Route found: {}", route);
        return mapper.convertToDto(route, RouteResponseDto.class);
    }

    @Override
    public RouteListResponseDto getAllRoutes() {
        log.info("Fetching all routes");
        List<Route> routes = routeRepository.findAll();
        log.debug("Found {} routes", routes.size());

        List<RouteResponseDto> routeResponseDto = routes.stream()
                .map(route -> mapper.convertToDto(route, RouteResponseDto.class))
                .toList();

        return RouteListResponseDto.builder()
                .route(routeResponseDto)
                .build();
    }

    @Override
    public RouteResponseDto updateRoute(Long id, RouteRequestDto routeRequestDto) {
        log.info("Updating route: id={}, data={}", id, routeRequestDto);
        Route existingRoute = routeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Route not found during update: id={}", id);
                    return new RouteNotFoundException("Route not found with id: " + id);
                });

        existingRoute.setStartAddress(routeRequestDto.getStartAddress());
        existingRoute.setEndAddress(routeRequestDto.getEndAddress());
        existingRoute.setDistance(routeRequestDto.getDistance());
        existingRoute.setEstimatedTime(routeRequestDto.getEstimatedTime());

        Route updatedRoute = routeRepository.save(existingRoute);
        log.debug("Route updated successfully: {}", updatedRoute);
        return mapper.convertToDto(updatedRoute, RouteResponseDto.class);
    }

    @Override
    public void deleteRoute(Long id) {
        log.info("Deleting route: id={}", id);
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Route not found during deletion: id={}", id);
                    return new RouteNotFoundException("Route not found with id: " + id);
                });
        routeRepository.delete(route);
        log.debug("Route deleted successfully: id={}", id);
    }
}