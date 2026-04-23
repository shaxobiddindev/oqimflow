package uz.transport.monitoring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class RouteDto {

    @Data
    public static class CreateRequest {
        @NotBlank
        @Size(max = 20)
        private String routeNumber;

        @NotBlank
        @Size(max = 150)
        private String name;

        @Size(max = 100)
        private String startPoint;

        @Size(max = 100)
        private String endPoint;

        private Double distanceKm;
        private Integer estimatedMinutes;
    }

    @Data
    public static class UpdateRequest {
        private String name;
        private String startPoint;
        private String endPoint;
        private Double distanceKm;
        private Integer estimatedMinutes;
        private Boolean active;
    }

    @Data
    public static class Response {
        private Long id;
        private String routeNumber;
        private String name;
        private String startPoint;
        private String endPoint;
        private Double distanceKm;
        private Integer estimatedMinutes;
        private boolean active;
        private Integer activeTransportCount;
    }
}
