package uz.transport.monitoring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.transport.monitoring.dto.RouteDto;
import uz.transport.monitoring.entity.Route;
import uz.transport.monitoring.enums.TransportStatus;
import uz.transport.monitoring.exception.BusinessException;
import uz.transport.monitoring.exception.ResourceNotFoundException;
import uz.transport.monitoring.repository.RouteRepository;
import uz.transport.monitoring.repository.TransportRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;
    private final TransportRepository transportRepository;

    public List<RouteDto.Response> getAll() {
        return routeRepository.findAllByActiveTrue()
                .stream().map(this::toResponse).toList();
    }

    public RouteDto.Response getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public RouteDto.Response create(RouteDto.CreateRequest request) {
        if (routeRepository.existsByRouteNumber(request.getRouteNumber())) {
            throw new BusinessException("Bu marshrut raqami allaqachon mavjud: " + request.getRouteNumber());
        }
        Route route = Route.builder()
                .routeNumber(request.getRouteNumber())
                .name(request.getName())
                .startPoint(request.getStartPoint())
                .endPoint(request.getEndPoint())
                .distanceKm(request.getDistanceKm())
                .estimatedMinutes(request.getEstimatedMinutes())
                .active(true)
                .build();
        return toResponse(routeRepository.save(route));
    }

    @Transactional
    public RouteDto.Response update(Long id, RouteDto.UpdateRequest request) {
        Route route = findById(id);
        if (request.getName() != null) route.setName(request.getName());
        if (request.getStartPoint() != null) route.setStartPoint(request.getStartPoint());
        if (request.getEndPoint() != null) route.setEndPoint(request.getEndPoint());
        if (request.getDistanceKm() != null) route.setDistanceKm(request.getDistanceKm());
        if (request.getEstimatedMinutes() != null) route.setEstimatedMinutes(request.getEstimatedMinutes());
        if (request.getActive() != null) route.setActive(request.getActive());
        return toResponse(routeRepository.save(route));
    }

    @Transactional
    public void delete(Long id) {
        Route route = findById(id);
        route.setActive(false);
        routeRepository.save(route);
        log.info("Route deactivated: {}", id);
    }

    public Route findById(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marshrut", id));
    }

    private RouteDto.Response toResponse(Route route) {
        RouteDto.Response res = new RouteDto.Response();
        res.setId(route.getId());
        res.setRouteNumber(route.getRouteNumber());
        res.setName(route.getName());
        res.setStartPoint(route.getStartPoint());
        res.setEndPoint(route.getEndPoint());
        res.setDistanceKm(route.getDistanceKm());
        res.setEstimatedMinutes(route.getEstimatedMinutes());
        res.setActive(route.isActive());
        int count = transportRepository.findAllByRouteIdAndActiveTrue(route.getId())
                .stream()
                .filter(t -> t.getStatus() == TransportStatus.ACTIVE)
                .toList().size();
        res.setActiveTransportCount(count);
        return res;
    }
}
