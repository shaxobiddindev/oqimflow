package uz.transport.monitoring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.transport.monitoring.dto.ApiResponse;
import uz.transport.monitoring.dto.TransportDto;
import uz.transport.monitoring.enums.TransportStatus;
import uz.transport.monitoring.service.TransportService;

import java.util.List;

@RestController
@RequestMapping("/v1/transports")
@RequiredArgsConstructor
@Tag(name = "Transport", description = "Transport boshqaruvi")
public class TransportController {

    private final TransportService transportService;

    @GetMapping
    @Operation(summary = "Barcha transportlar ro'yxati")
    public ResponseEntity<ApiResponse<List<TransportDto.Response>>> getAll(
            @RequestParam(required = false) TransportStatus status,
            @RequestParam(required = false) Long routeId) {

        List<TransportDto.Response> result;
        if (status != null) {
            result = transportService.getByStatus(status);
        } else if (routeId != null) {
            result = transportService.getByRoute(routeId);
        } else {
            result = transportService.getAll();
        }
        return ResponseEntity.ok(ApiResponse.ok(result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Transport ma'lumoti ID bo'yicha")
    public ResponseEntity<ApiResponse<TransportDto.Response>> getById(
            @Parameter(description = "Transport ID") @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(transportService.getById(id)));
    }

    @PostMapping
    @Operation(summary = "Yangi transport qo'shish")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<ApiResponse<TransportDto.Response>> create(
            @Valid @RequestBody TransportDto.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Transport qo'shildi", transportService.create(request)));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Transport ma'lumotlarini yangilash")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<ApiResponse<TransportDto.Response>> update(
            @PathVariable Long id,
            @RequestBody TransportDto.UpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Transport yangilandi", transportService.update(id, request)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Transport holatini o'zgartirish")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<ApiResponse<TransportDto.Response>> updateStatus(
            @PathVariable Long id,
            @RequestParam TransportStatus status) {
        return ResponseEntity.ok(ApiResponse.ok(transportService.updateStatus(id, status)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Transportni o'chirish (deaktivatsiya)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        transportService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Transport o'chirildi", null));
    }
}
