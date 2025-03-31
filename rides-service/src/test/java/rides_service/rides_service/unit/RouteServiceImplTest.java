package rides_service.rides_service.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rides_service.rides_service.config.mapper.DtoMapper;
import rides_service.rides_service.dto.route.RouteListResponseDto;
import rides_service.rides_service.dto.route.RouteRequestDto;
import rides_service.rides_service.dto.route.RouteResponseDto;
import rides_service.rides_service.entity.Route;
import rides_service.rides_service.exception.route.RouteNotFoundException;
import rides_service.rides_service.repository.RouteRepository;
import rides_service.rides_service.service.impl.RouteServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RouteServiceImplTest {

    @Mock
    private RouteRepository routeRepository;

    @InjectMocks
    private RouteServiceImpl routeService;

    @Mock
    private DtoMapper mapper;

    @Test
    void getRouteById_WhenRouteExists() {
        Long routeId = 1L;
        Route route = new Route();
        route.setId(routeId);

        when(routeRepository.findById(routeId)).thenReturn(Optional.of(route));
        when(mapper.convertToDto(any(Route.class), eq(RouteResponseDto.class))).thenReturn(new RouteResponseDto());

        RouteResponseDto result = routeService.getRouteById(routeId);

        assertNotNull(result);
        verify(routeRepository, times(1)).findById(routeId);
        verify(mapper, times(1)).convertToDto(any(Route.class), eq(RouteResponseDto.class));
    }

    @Test
    void createRoute_WhenValidRequest() {
        RouteRequestDto routeRequestDto = new RouteRequestDto();
        routeRequestDto.setStartAddress("Start");
        routeRequestDto.setEndAddress("End");
        routeRequestDto.setDistance(10.0f);
        routeRequestDto.setEstimatedTime(30);

        Route route = new Route();
        route.setId(1L);

        when(mapper.convertToEntity(any(RouteRequestDto.class), eq(Route.class))).thenReturn(route);
        when(routeRepository.save(any(Route.class))).thenReturn(route);
        when(mapper.convertToDto(any(Route.class), eq(RouteResponseDto.class))).thenReturn(new RouteResponseDto());

        RouteResponseDto result = routeService.createRoute(routeRequestDto);

        assertNotNull(result);
        verify(routeRepository, times(1)).save(any(Route.class));
        verify(mapper, times(1)).convertToEntity(any(RouteRequestDto.class), eq(Route.class));
        verify(mapper, times(1)).convertToDto(any(Route.class), eq(RouteResponseDto.class));
    }

    @Test
    void getRouteById_WhenRouteNotFound() {
        Long routeId = 1L;
        when(routeRepository.findById(routeId)).thenReturn(Optional.empty());

        assertThrows(RouteNotFoundException.class, () -> routeService.getRouteById(routeId));
        verify(routeRepository, times(1)).findById(routeId);
    }

    @Test
    void getAllRoutes() {
        Route route = new Route();
        route.setId(1L);
        route.setStartAddress("Start");
        route.setEndAddress("End");

        List<Route> routes = Collections.singletonList(route);
        when(routeRepository.findAll()).thenReturn(routes);
        when(mapper.convertToDto(any(Route.class), eq(RouteResponseDto.class))).thenReturn(new RouteResponseDto());


        RouteListResponseDto result = routeService.getAllRoutes();

        assertNotNull(result);
        assertEquals(1, result.getRoute().size());
        verify(routeRepository, times(1)).findAll();
        verify(mapper, times(1)).convertToDto(any(Route.class), eq(RouteResponseDto.class));
    }

    @Test
    void updateRoute() {
        Long routeId = 1L;
        RouteRequestDto routeRequestDto = new RouteRequestDto();
        routeRequestDto.setStartAddress("New Start");
        routeRequestDto.setEndAddress("New End");
        routeRequestDto.setDistance(20.0f);
        routeRequestDto.setEstimatedTime(40);

        Route existingRoute = new Route();
        existingRoute.setId(routeId);

        when(routeRepository.findById(routeId)).thenReturn(Optional.of(existingRoute));
        when(routeRepository.save(any(Route.class))).thenReturn(existingRoute);
        when(mapper.convertToDto(any(Route.class), eq(RouteResponseDto.class))).thenReturn(new RouteResponseDto());

        RouteResponseDto result = routeService.updateRoute(routeId, routeRequestDto);

        assertNotNull(result);
        verify(routeRepository, times(1)).findById(routeId);
        verify(routeRepository, times(1)).save(any(Route.class));
        verify(mapper, times(1)).convertToDto(any(Route.class), eq(RouteResponseDto.class));
    }

    @Test
    void deleteRoute() {
        Long routeId = 1L;
        Route route = new Route();
        route.setId(routeId);

        when(routeRepository.findById(routeId)).thenReturn(Optional.of(route));
        doNothing().when(routeRepository).delete(route);

        routeService.deleteRoute(routeId);

        verify(routeRepository, times(1)).findById(routeId);
        verify(routeRepository, times(1)).delete(route);
    }
}