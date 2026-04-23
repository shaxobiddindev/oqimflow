package uz.transport.monitoring.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class LocationDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        @NotNull(message = "Transport ID ko'rsatilsin")
        private Long transportId;

        @NotNull
        private Double latitude;

        @NotNull
        private Double longitude;

        private Double speed;
        private Integer passengerCount;
        private LocalDateTime recordedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long transportId;
        private String plateNumber;
        private String routeNumber;
        private Double latitude;
        private Double longitude;
        private Double speed;
        private Integer passengerCount;
        private Integer capacity;
        private Double loadPercent;
        private String status;
        private LocalDateTime recordedAt;
    }
}
