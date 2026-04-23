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
import uz.transport.monitoring.dto.RouteDto;
import uz.transport.monitoring.service.RouteService;

import java.util.List;

@RestController
@RequestMapping("/v1/routes")
@RequiredArgsConstructor
@Tag(name = "Route", description = "Marshrut boshqaruvi")
public class RouteController {

    private final RouteService routeService;

    @GetMapping
    @Operation(summary = "Barcha marshrutlar")
    public ResponseEntity<ApiResponse<List<RouteDto.Response>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(routeService.getAll()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Marshrut ID bo'yicha")
    public ResponseEntity<ApiResponse<RouteDto.Response>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(routeService.getById(id)));
    }

    @PostMapping
    @Operation(summary = "Yangi marshrut qo'shish")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<ApiResponse<RouteDto.Response>> create(
            @Valid @RequestBody RouteDto.CreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Marshrut qo'shildi", routeService.create(request)));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Marshrut ma'lumotlarini yangilash")
    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
    public ResponseEntity<ApiResponse<RouteDto.Response>> update(
            @PathVariable Long id,
            @RequestBody RouteDto.UpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(routeService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Marshrut o'chirish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        routeService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Marshrut o'chirildi", null));
    }
}
