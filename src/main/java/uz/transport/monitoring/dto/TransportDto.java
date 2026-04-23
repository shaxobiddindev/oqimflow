package uz.transport.monitoring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import uz.transport.monitoring.enums.TransportType;

public class TransportDto {

    @Data
    public static class CreateRequest {
        @NotBlank(message = "Davlat raqami bo'sh bo'lmasin")
        @Size(max = 20)
        private String plateNumber;

        @NotBlank(message = "Model bo'sh bo'lmasin")
        @Size(max = 100)
        private String model;

        @NotNull(message = "Transport turi ko'rsatilsin")
        private TransportType type;

        @NotNull(message = "Sig'im ko'rsatilsin")
        private Integer capacity;

        private Long routeId;

        private String deviceId;
    }

    @Data
    public static class UpdateRequest {
        private String model;
        private Integer capacity;
        private Long routeId;
        private String deviceId;
        private Boolean active;
    }

    @Data
    public static class Response {
        private Long id;
        private String plateNumber;
        private String model;
        private TransportType type;
        private Integer capacity;
        private Long routeId;
        private String routeNumber;
        private String routeName;
        private String status;
        private Double lastLatitude;
        private Double lastLongitude;
        private Integer currentPassengers;
        private String deviceId;
        private boolean active;
    }
}
