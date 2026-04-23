package uz.transport.monitoring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.transport.monitoring.dto.ApiResponse;
import uz.transport.monitoring.dto.StationDto;
import uz.transport.monitoring.service.StationService;

import java.util.List;

@RestController
@RequestMapping("/v1/stations")
@RequiredArgsConstructor
@Tag(name = "Station", description = "Bekat boshqaruvi")
public class StationController {

    private final StationService stationService;

    @GetMapping
    @Operation(summary = "Barcha bekatlar")
    public ResponseEntity<ApiResponse<List<StationDto.Response>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(stationService.getAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Bekat ID bo'yicha")
    public ResponseEntity<ApiResponse<StationDto.Response>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(stationService.getById(id)));
    }

    @GetMapping("/nearby")
    @Operation(summary = "Yaqin atrofdagi bekatlar",
            description = "Koordinata va radius (km) bo'yicha bekatlar qidirish")
    public ResponseEntity<ApiResponse<List<StationDto.Response>>> getNearby(
            @RequestParam Double lat,
            @RequestParam Double lng,
            @RequestParam(defaultValue = "1.0") Double radius) {
        return ResponseEntity.ok(ApiResponse.ok(stationService.getNearby(lat, lng, radius)));
    }

    @PostMapping
    @Operation(summary = "Yangi bekat qo'shish")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<ApiResponse<StationDto.Response>> create(
            @Valid @RequestBody StationDto.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Bekat qo'shildi", stationService.create(request)));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Bekat ma'lumotlarini yangilash")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<ApiResponse<StationDto.Response>> update(
            @PathVariable Long id,
            @RequestBody StationDto.UpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(stationService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Bekatni o'chirish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        stationService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Bekat o'chirildi", null));
    }
}
