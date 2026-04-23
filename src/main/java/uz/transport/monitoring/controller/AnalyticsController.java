package uz.transport.monitoring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.transport.monitoring.dto.AnalyticsDto;
import uz.transport.monitoring.dto.ApiResponse;
import uz.transport.monitoring.service.AnalyticsService;

@RestController
@RequestMapping("/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Statistika va analitika")
@PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR')")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    @Operation(summary = "Dashboard statistikasi",
            description = "Umumiy statistika: transport holati, yo'lovchilar, eng band bekatlar, soatlik grafik")
    public ResponseEntity<ApiResponse<AnalyticsDto>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.ok(analyticsService.getDashboard()));
    }
}
