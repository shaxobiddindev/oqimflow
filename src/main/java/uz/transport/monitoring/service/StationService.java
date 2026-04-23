package uz.transport.monitoring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.transport.monitoring.dto.StationDto;
import uz.transport.monitoring.entity.Station;
import uz.transport.monitoring.exception.ResourceNotFoundException;
import uz.transport.monitoring.repository.StationRepository;
import uz.transport.monitoring.repository.PassengerLogRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StationService {

    private final StationRepository stationRepository;
    private final PassengerLogRepository passengerLogRepository;

    public List<StationDto.Response> getAll() {
        return stationRepository.findAllByActiveTrue()
                .stream().map(this::toResponse).toList();
    }

    public StationDto.Response getById(Long id) {
        return toResponse(findById(id));
    }

    public List<StationDto.Response> getNearby(Double lat, Double lng, Double radiusKm) {
        return stationRepository.findNearbyStations(lat, lng, radiusKm)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public StationDto.Response create(StationDto.CreateRequest request) {
        Station station = Station.builder()
                .name(request.getName())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .address(request.getAddress())
                .active(true)
                .build();
        return toResponse(stationRepository.save(station));
    }

    @Transactional
    public StationDto.Response update(Long id, StationDto.UpdateRequest request) {
        Station station = findById(id);
        if (request.getName() != null) station.setName(request.getName());
        if (request.getLatitude() != null) station.setLatitude(request.getLatitude());
        if (request.getLongitude() != null) station.setLongitude(request.getLongitude());
        if (request.getAddress() != null) station.setAddress(request.getAddress());
        if (request.getActive() != null) station.setActive(request.getActive());
        return toResponse(stationRepository.save(station));
    }

    @Transactional
    public void delete(Long id) {
        Station station = findById(id);
        station.setActive(false);
        stationRepository.save(station);
    }

    public Station findById(Long id) {
        return stationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bekat", id));
    }

    private StationDto.Response toResponse(Station s) {
        StationDto.Response res = new StationDto.Response();
        res.setId(s.getId());
        res.setName(s.getName());
        res.setLatitude(s.getLatitude());
        res.setLongitude(s.getLongitude());
        res.setAddress(s.getAddress());
        res.setActive(s.isActive());
        
        // Jami chiqishlar sonini hisoblash
        Long boarded = passengerLogRepository.totalBoardedByStation(s.getId());
        res.setTotalBoardings(boarded != null ? boarded : 0L);
        
        return res;
    }
}
