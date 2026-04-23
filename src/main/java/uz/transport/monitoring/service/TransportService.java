package uz.transport.monitoring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.transport.monitoring.dto.TransportDto;
import uz.transport.monitoring.entity.Route;
import uz.transport.monitoring.entity.Transport;
import uz.transport.monitoring.enums.TransportStatus;
import uz.transport.monitoring.exception.BusinessException;
import uz.transport.monitoring.exception.ResourceNotFoundException;
import uz.transport.monitoring.repository.TransportRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransportService {

    private final TransportRepository transportRepository;
    private final RouteService routeService;

    @Transactional(readOnly = true)
    public List<TransportDto.Response> getAll() {
        return transportRepository.findAllByActiveTrue()
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<TransportDto.Response> getByStatus(TransportStatus status) {
        return transportRepository.findAllByStatusAndActiveTrue(status)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<TransportDto.Response> getByRoute(Long routeId) {
        return transportRepository.findAllByRouteIdAndActiveTrue(routeId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public TransportDto.Response getById(Long id) {
        return toResponse(findById(id));
    }

    @Transactional
    public TransportDto.Response create(TransportDto.CreateRequest request) {
        if (transportRepository.findByPlateNumberAndActiveTrue(request.getPlateNumber()).isPresent()) {
            throw new BusinessException("Bu davlat raqami allaqachon mavjud: " + request.getPlateNumber());
        }

        Transport transport = Transport.builder()
                .plateNumber(request.getPlateNumber())
                .model(request.getModel())
                .type(request.getType())
                .capacity(request.getCapacity())
                .deviceId(request.getDeviceId())
                .status(TransportStatus.OFFLINE)
                .currentPassengers(0)
                .active(true)
                .build();

        if (request.getRouteId() != null) {
            Route route = routeService.findById(request.getRouteId());
            transport.setRoute(route);
        }

        return toResponse(transportRepository.save(transport));
    }

    @Transactional
    public TransportDto.Response update(Long id, TransportDto.UpdateRequest request) {
        Transport transport = findById(id);

        if (request.getModel() != null) transport.setModel(request.getModel());
        if (request.getCapacity() != null) transport.setCapacity(request.getCapacity());
        if (request.getDeviceId() != null) transport.setDeviceId(request.getDeviceId());
        if (request.getActive() != null) transport.setActive(request.getActive());

        if (request.getRouteId() != null) {
            Route route = routeService.findById(request.getRouteId());
            transport.setRoute(route);
        }

        return toResponse(transportRepository.save(transport));
    }

    @Transactional
    public void delete(Long id) {
        Transport transport = findById(id);
        transport.setActive(false);
        transport.setStatus(TransportStatus.OFFLINE);
        transportRepository.save(transport);
        log.info("Transport deactivated: {}", id);
    }

    @Transactional
    public TransportDto.Response updateStatus(Long id, TransportStatus status) {
        Transport transport = findById(id);
        transport.setStatus(status);
        return toResponse(transportRepository.save(transport));
    }

    public Transport findById(Long id) {
        return transportRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transport", id));
    }

    public List<Transport> findAllActive() {
        return transportRepository.findAllActiveMoving();
    }

    private TransportDto.Response toResponse(Transport t) {
        TransportDto.Response res = new TransportDto.Response();
        res.setId(t.getId());
        res.setPlateNumber(t.getPlateNumber());
        res.setModel(t.getModel());
        res.setType(t.getType());
        res.setCapacity(t.getCapacity());
        res.setStatus(t.getStatus().name());
        res.setLastLatitude(t.getLastLatitude());
        res.setLastLongitude(t.getLastLongitude());
        res.setCurrentPassengers(t.getCurrentPassengers());
        res.setDeviceId(t.getDeviceId());
        res.setActive(t.isActive());
        if (t.getRoute() != null) {
            res.setRouteId(t.getRoute().getId());
            res.setRouteNumber(t.getRoute().getRouteNumber());
            res.setRouteName(t.getRoute().getName());
        }
        return res;
    }
}
