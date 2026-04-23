package uz.transport.monitoring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.transport.monitoring.dto.ApiResponse;
import uz.transport.monitoring.dto.PassengerDto;
import uz.transport.monitoring.service.PassengerService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/passengers")
@RequiredArgsConstructor
@Tag(name = "Passenger", description = "Yo'lovchi oqimi ma'lumotlari")
public class PassengerController {

    private final PassengerService passengerService;

    @PostMapping
    @Operation(summary = "Yo'lovchi sonini yangilash",
            description = "Bekatda tushgan/chiqqan yo'lovchilar sonini qabul qilish")
    public ResponseEntity<ApiResponse<PassengerDto.Response>> update(
            @Valid @RequestBody PassengerDto.UpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(passengerService.update(request)));
    }

    @GetMapping("/{transportId}/history")
    @Operation(summary = "Yo'lovchi tarixi")
    public ResponseEntity<ApiResponse<List<PassengerDto.Response>>> getHistory(
            @PathVariable Long transportId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(ApiResponse.ok(passengerService.getHistory(transportId, from, to)));
    }
}
