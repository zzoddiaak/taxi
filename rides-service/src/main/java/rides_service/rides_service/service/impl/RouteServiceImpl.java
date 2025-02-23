package rides_service.rides_service.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rides_service.rides_service.config.mapper.DtoMapper;
import rides_service.rides_service.dto.route.RouteRequestDto;
import rides_service.rides_service.dto.route.RouteResponseDto;
import rides_service.rides_service.entity.Route;
import rides_service.rides_service.exception.route.RouteNotFoundException;
import rides_service.rides_service.repository.RouteRepository;
import rides_service.rides_service.service.api.RouteService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final DtoMapper mapper;

    @Override
    public RouteResponseDto createRoute(RouteRequestDto routeRequestDto) {
        Route route = mapper.convertToEntity(routeRequestDto, Route.class);
        Route savedRoute = routeRepository.save(route);
        return mapper.convertToDto(savedRoute, RouteResponseDto.class);
    }

    @Override
    public RouteResponseDto getRouteById(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new RouteNotFoundException("Route not found with id: " + id));
        return mapper.convertToDto(route, RouteResponseDto.class);
    }

    @Override
    public List<RouteResponseDto> getAllRoutes() {
        List<Route> routes = routeRepository.findAll();
        return routes.stream()
                .map(route -> mapper.convertToDto(route, RouteResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public RouteResponseDto updateRoute(Long id, RouteRequestDto routeRequestDto) {
        Route existingRoute = routeRepository.findById(id)
                .orElseThrow(() -> new RouteNotFoundException("Route not found with id: " + id));

        existingRoute.setStartAddress(routeRequestDto.getStartAddress());
        existingRoute.setEndAddress(routeRequestDto.getEndAddress());
        existingRoute.setDistance(routeRequestDto.getDistance());
        existingRoute.setEstimatedTime(routeRequestDto.getEstimatedTime());

        Route updatedRoute = routeRepository.save(existingRoute);
        return mapper.convertToDto(updatedRoute, RouteResponseDto.class);
    }

    @Override
    public void deleteRoute(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new RouteNotFoundException("Route not found with id: " + id));
        routeRepository.delete(route);
    }
}