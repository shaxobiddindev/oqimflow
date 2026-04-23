package uz.transport.monitoring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsDto {

    // Dashboard uchun umumiy statistika
    private Long totalTransports;
    private Long activeTransports;
    private Long idleTransports;
    private Long offlineTransports;

    private Integer totalPassengersToday;
    private Double avgLoadPercent;

    private List<HourlyStats> hourlyPassengers;
    private List<TopStation> topStations;
    private List<TransportStats> transportStats;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HourlyStats {
        private Integer hour;
        private Double avgPassengers;
        private String label; // "08:00", "09:00" etc.
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopStation {
        private Long stationId;
        private String stationName;
        private Long totalBoardings;
        private Double avgLoad;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransportStats {
        private Long transportId;
        private String plateNumber;
        private String routeNumber;
        private Integer totalPassengersToday;
        private Double avgLoadPercent;
        private Integer currentPassengers;
        private Integer capacity;
    }
}
