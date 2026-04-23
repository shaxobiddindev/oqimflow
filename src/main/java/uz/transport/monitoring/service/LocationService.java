package uz.transport.monitoring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.transport.monitoring.dto.LocationDto;
import uz.transport.monitoring.entity.LocationLog;
import uz.transport.monitoring.entity.Transport;
import uz.transport.monitoring.enums.TransportStatus;
import uz.transport.monitoring.exception.ResourceNotFoundException;
import uz.transport.monitoring.repository.LocationLogRepository;
import uz.transport.monitoring.repository.TransportRepository;
import uz.transport.monitoring.websocket.LocationPublisher;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationLogRepository locationLogRepository;
    private final TransportRepository transportRepository;
    private final LocationPublisher locationPublisher;

    @Transactional
    public LocationDto.Response updateLocation(LocationDto.UpdateRequest request) {
        Transport transport = transportRepository.findById(request.getTransportId())
                .orElseThrow(() -> new ResourceNotFoundException("Transport", request.getTransportId()));

        // Transport holatini yangilash
        transport.setLastLatitude(request.getLatitude());
        transport.setLastLongitude(request.getLongitude());
        transport.setStatus(TransportStatus.ACTIVE);
        if (request.getPassengerCount() != null) {
            transport.setCurrentPassengers(request.getPassengerCount());
        }
        transportRepository.save(transport);

        // Log yozish
        LocationLog log = LocationLog.builder()
                .transport(transport)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .speed(request.getSpeed())
                .passengerCount(transport.getCurrentPassengers())
                .recordedAt(request.getRecordedAt() != null ? request.getRecordedAt() : LocalDateTime.now())
                .build();
        locationLogRepository.save(log);

        LocationDto.Response response = buildResponse(transport, log);

        // Real-time push
        locationPublisher.sendTransportUpdate(transport.getId(), response);

        return response;
    }

    public List<LocationDto.Response> getAllCurrentLocations() {
        return transportRepository.findAllByStatusAndActiveTrue(TransportStatus.ACTIVE)
                .stream()
                .map(t -> {
                    LocationDto.Response res = new LocationDto.Response();
                    res.setTransportId(t.getId());
                    res.setPlateNumber(t.getPlateNumber());
                    res.setRouteNumber(t.getRoute() != null ? t.getRoute().getRouteNumber() : null);
                    res.setLatitude(t.getLastLatitude());
                    res.setLongitude(t.getLastLongitude());
                    res.setPassengerCount(t.getCurrentPassengers());
                    res.setCapacity(t.getCapacity());
                    res.setLoadPercent(calcLoad(t.getCurrentPassengers(), t.getCapacity()));
                    res.setStatus(t.getStatus().name());
                    res.setRecordedAt(LocalDateTime.now());
                    return res;
                }).toList();
    }

    public List<LocationDto.Response> getHistory(Long transportId, LocalDateTime from, LocalDateTime to) {
        Transport transport = transportRepository.findById(transportId)
                .orElseThrow(() -> new ResourceNotFoundException("Transport", transportId));

        return locationLogRepository
                .findByTransportIdAndRecordedAtBetweenOrderByRecordedAtDesc(transportId, from, to)
                .stream()
                .map(l -> buildResponse(transport, l))
                .toList();
    }

    public LocationDto.Response getLatest(Long transportId) {
        Transport transport = transportRepository.findById(transportId)
                .orElseThrow(() -> new ResourceNotFoundException("Transport", transportId));

        List<LocationLog> logs = locationLogRepository
                .findLatestByTransportId(transportId, PageRequest.of(0, 1));

        if (logs.isEmpty()) {
            throw new ResourceNotFoundException("Bu transport uchun joylashuv ma'lumoti topilmadi");
        }
        return buildResponse(transport, logs.get(0));
    }

    private LocationDto.Response buildResponse(Transport t, LocationLog l) {
        return LocationDto.Response.builder()
                .transportId(t.getId())
                .plateNumber(t.getPlateNumber())
                .routeNumber(t.getRoute() != null ? t.getRoute().getRouteNumber() : null)
                .latitude(l.getLatitude())
                .longitude(l.getLongitude())
                .speed(l.getSpeed())
                .passengerCount(l.getPassengerCount())
                .capacity(t.getCapacity())
                .loadPercent(calcLoad(l.getPassengerCount(), t.getCapacity()))
                .status(t.getStatus().name())
                .recordedAt(l.getRecordedAt())
                .build();
    }

    private Double calcLoad(Integer passengers, Integer capacity) {
        if (passengers == null || capacity == null || capacity == 0) return 0.0;
        return Math.round((passengers * 100.0 / capacity) * 10.0) / 10.0;
    }
}
