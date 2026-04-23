package uz.transport.monitoring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.transport.monitoring.dto.PassengerDto;
import uz.transport.monitoring.entity.PassengerLog;
import uz.transport.monitoring.entity.Station;
import uz.transport.monitoring.entity.Transport;
import uz.transport.monitoring.exception.ResourceNotFoundException;
import uz.transport.monitoring.repository.PassengerLogRepository;
import uz.transport.monitoring.repository.StationRepository;
import uz.transport.monitoring.repository.TransportRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PassengerService {

    private final PassengerLogRepository passengerLogRepository;
    private final TransportRepository transportRepository;
    private final StationRepository stationRepository;

    @Transactional
    public PassengerDto.Response update(PassengerDto.UpdateRequest request) {
        Transport transport = transportRepository.findById(request.getTransportId())
                .orElseThrow(() -> new ResourceNotFoundException("Transport", request.getTransportId()));

        transport.setCurrentPassengers(request.getTotalOnboard());
        transportRepository.save(transport);

        Station station = null;
        if (request.getStationId() != null) {
            station = stationRepository.findById(request.getStationId()).orElse(null);
        }

        PassengerLog passengerLog = PassengerLog.builder()
                .transport(transport)
                .station(station)
                .boardedCount(request.getBoardedCount() != null ? request.getBoardedCount() : 0)
                .alightedCount(request.getAlightedCount() != null ? request.getAlightedCount() : 0)
                .totalOnboard(request.getTotalOnboard())
                .recordedAt(request.getRecordedAt() != null ? request.getRecordedAt() : LocalDateTime.now())
                .build();

        passengerLogRepository.save(passengerLog);
        log.debug("Passenger update: transport={}, onboard={}", transport.getPlateNumber(), request.getTotalOnboard());

        return toResponse(passengerLog);
    }

    @Transactional(readOnly = true)
    public List<PassengerDto.Response> getHistory(Long transportId, LocalDateTime from, LocalDateTime to) {
        if (!transportRepository.existsById(transportId)) {
            throw new ResourceNotFoundException("Transport", transportId);
        }
        return passengerLogRepository
                .findByTransportIdAndRecordedAtBetweenOrderByRecordedAtDesc(transportId, from, to)
                .stream().map(this::toResponse).toList();
    }

    private PassengerDto.Response toResponse(PassengerLog p) {
        return PassengerDto.Response.builder()
                .id(p.getId())
                .transportId(p.getTransport().getId())
                .plateNumber(p.getTransport().getPlateNumber())
                .stationId(p.getStation() != null ? p.getStation().getId() : null)
                .stationName(p.getStation() != null ? p.getStation().getName() : null)
                .boardedCount(p.getBoardedCount())
                .alightedCount(p.getAlightedCount())
                .totalOnboard(p.getTotalOnboard())
                .recordedAt(p.getRecordedAt())
                .build();
    }
}
