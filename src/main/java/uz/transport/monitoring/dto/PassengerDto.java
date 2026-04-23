package uz.transport.monitoring.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class PassengerDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        @NotNull(message = "Transport ID ko'rsatilsin")
        private Long transportId;

        private Long stationId;

        @Min(0)
        private Integer boardedCount;

        @Min(0)
        private Integer alightedCount;

        @NotNull
        private Integer totalOnboard;

        private LocalDateTime recordedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long transportId;
        private String plateNumber;
        private Long stationId;
        private String stationName;
        private Integer boardedCount;
        private Integer alightedCount;
        private Integer totalOnboard;
        private LocalDateTime recordedAt;
    }
}
