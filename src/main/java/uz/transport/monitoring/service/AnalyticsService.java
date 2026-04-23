package uz.transport.monitoring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uz.transport.monitoring.dto.AnalyticsDto;
import uz.transport.monitoring.entity.Transport;
import uz.transport.monitoring.enums.TransportStatus;
import uz.transport.monitoring.repository.LocationLogRepository;
import uz.transport.monitoring.repository.PassengerLogRepository;
import uz.transport.monitoring.repository.TransportRepository;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsService {

    private final TransportRepository transportRepository;
    private final LocationLogRepository locationLogRepository;
    private final PassengerLogRepository passengerLogRepository;

    public AnalyticsDto getDashboard() {
        LocalDateTime todayStart = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);

        long total = transportRepository.count();
        long active = transportRepository.countByStatus(TransportStatus.ACTIVE);
        long idle = transportRepository.countByStatus(TransportStatus.IDLE);
        long offline = transportRepository.countByStatus(TransportStatus.OFFLINE);

        // Bugungi jami yo'lovchilar
        List<Transport> allTransports = transportRepository.findAllByActiveTrue();
        int totalPassengersToday = allTransports.stream()
                .mapToInt(t -> {
                    Long count = passengerLogRepository.totalBoardedByTransport(t.getId(), todayStart);
                    return count != null ? count.intValue() : 0;
                }).sum();

        // O'rtacha yuklama
        double avgLoad = allTransports.stream()
                .filter(t -> t.getCapacity() != null && t.getCapacity() > 0)
                .mapToDouble(t -> ((t.getCurrentPassengers() != null ? t.getCurrentPassengers() : 0) * 100.0) / t.getCapacity())
                .average().orElse(0.0);

        // Soatlik statistika (oxirgi 7 kun)
        List<AnalyticsDto.HourlyStats> hourlyStats = buildHourlyStats(weekAgo);

        // Top bekatlari
        List<AnalyticsDto.TopStation> topStations = buildTopStations(weekAgo);

        // Transport statistikalari
        List<AnalyticsDto.TransportStats> transportStats = buildTransportStats(allTransports, todayStart);

        return AnalyticsDto.builder()
                .totalTransports(total)
                .activeTransports(active)
                .idleTransports(idle)
                .offlineTransports(offline)
                .totalPassengersToday(totalPassengersToday)
                .avgLoadPercent(Math.round(avgLoad * 10.0) / 10.0)
                .hourlyPassengers(hourlyStats)
                .topStations(topStations)
                .transportStats(transportStats)
                .build();
    }

    private List<AnalyticsDto.HourlyStats> buildHourlyStats(LocalDateTime from) {
        List<Object[]> raw = locationLogRepository.getHourlyAvgPassengers(from);
        List<AnalyticsDto.HourlyStats> result = new ArrayList<>();
        for (Object[] row : raw) {
            int hour = ((Number) row[0]).intValue();
            double avg = row[1] != null ? ((Number) row[1]).doubleValue() : 0.0;
            result.add(AnalyticsDto.HourlyStats.builder()
                    .hour(hour)
                    .avgPassengers(Math.round(avg * 10.0) / 10.0)
                    .label(String.format("%02d:00", hour))
                    .build());
        }
        return result;
    }

    private List<AnalyticsDto.TopStation> buildTopStations(LocalDateTime from) {
        List<Object[]> raw = passengerLogRepository.topStationsByBoardings(from);
        List<AnalyticsDto.TopStation> result = new ArrayList<>();
        int limit = Math.min(raw.size(), 10);
        for (int i = 0; i < limit; i++) {
            Object[] row = raw.get(i);
            result.add(AnalyticsDto.TopStation.builder()
                    .stationId(((Number) row[0]).longValue())
                    .stationName((String) row[1])
                    .totalBoardings(row[2] != null ? ((Number) row[2]).longValue() : 0L)
                    .avgLoad(row.length > 3 && row[3] != null ? Math.round(((Number) row[3]).doubleValue() * 10.0) / 10.0 : 0.0)
                    .build());
        }
        return result;
    }

    private List<AnalyticsDto.TransportStats> buildTransportStats(List<Transport> transports, LocalDateTime todayStart) {
        List<AnalyticsDto.TransportStats> result = new ArrayList<>();
        for (Transport t : transports) {
            Long boarded = passengerLogRepository.totalBoardedByTransport(t.getId(), todayStart);
            double load = t.getCapacity() != null && t.getCapacity() > 0
                    ? Math.round(((t.getCurrentPassengers() != null ? t.getCurrentPassengers() : 0) * 100.0 / t.getCapacity()) * 10.0) / 10.0
                    : 0.0;
            result.add(AnalyticsDto.TransportStats.builder()
                    .transportId(t.getId())
                    .plateNumber(t.getPlateNumber())
                    .routeNumber(t.getRoute() != null ? t.getRoute().getRouteNumber() : null)
                    .totalPassengersToday(boarded != null ? boarded.intValue() : 0)
                    .avgLoadPercent(load)
                    .currentPassengers(t.getCurrentPassengers())
                    .capacity(t.getCapacity())
                    .build());
        }
        return result;
    }
}
