package uz.transport.monitoring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.transport.monitoring.dto.ApiResponse;
import uz.transport.monitoring.dto.LocationDto;
import uz.transport.monitoring.service.LocationService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/locations")
@RequiredArgsConstructor
@Tag(name = "Location", description = "GPS joylashuv ma'lumotlari")
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    @Operation(summary = "Transport joylashuvini yangilash",
            description = "GPS qurilmadan joylashuv ma'lumoti qabul qilish")
    public ResponseEntity<ApiResponse<LocationDto.Response>> updateLocation(
            @Valid @RequestBody LocationDto.UpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(locationService.updateLocation(request)));
    }

    @GetMapping("/current")
    @Operation(summary = "Barcha transportlarning hozirgi joylashuvi")
    public ResponseEntity<ApiResponse<List<LocationDto.Response>>> getCurrentLocations() {
        return ResponseEntity.ok(ApiResponse.ok(locationService.getAllCurrentLocations()));
    }

    @GetMapping("/{transportId}/latest")
    @Operation(summary = "Transport so'ngi joylashuvi")
    public ResponseEntity<ApiResponse<LocationDto.Response>> getLatest(@PathVariable Long transportId) {
        return ResponseEntity.ok(ApiResponse.ok(locationService.getLatest(transportId)));
    }

    @GetMapping("/{transportId}/history")
    @Operation(summary = "Transport joylashuv tarixi")
    public ResponseEntity<ApiResponse<List<LocationDto.Response>>> getHistory(
            @PathVariable Long transportId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(ApiResponse.ok(locationService.getHistory(transportId, from, to)));
    }
}
