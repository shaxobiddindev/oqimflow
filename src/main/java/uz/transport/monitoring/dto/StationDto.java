package uz.transport.monitoring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class StationDto {

    @Data
    public static class CreateRequest {
        @NotBlank
        private String name;

        @NotNull
        private Double latitude;

        @NotNull
        private Double longitude;

        private String address;
    }

    @Data
    public static class UpdateRequest {
        private String name;
        private Double latitude;
        private Double longitude;
        private String address;
        private Boolean active;
    }

    @Data
    public static class Response {
        private Long id;
        private String name;
        private Double latitude;
        private Double longitude;
        private String address;
        private boolean active;
        private Long totalBoardings;
    }
}
