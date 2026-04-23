package uz.transport.monitoring.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.transport.monitoring.dto.PassengerDto;
import uz.transport.monitoring.entity.Station;
import uz.transport.monitoring.entity.Transport;
import uz.transport.monitoring.repository.StationRepository;
import uz.transport.monitoring.repository.TransportRepository;
import uz.transport.monitoring.service.PassengerService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * Yo'lovchilar oqimi simulyatori. 
 * Avtomatik tarzda transportlarga yo'lovchilar chiqishi va tushishini (bekatlarda) hosil qiladi
 * Tizimni test qilish uchun mo'ljallangan.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PassengerSimulatorScheduler {

    private final TransportRepository transportRepository;
    private final StationRepository stationRepository;
    private final PassengerService passengerService;
    private final Random random = new Random();

    @Scheduled(fixedRateString = "${app.scheduler.passenger-interval:10000}")
    public void simulatePassengers() {
        List<Transport> activeTransports = transportRepository.findAllByActiveTrue();
        List<Station> allStations = stationRepository.findAll();

        if (activeTransports.isEmpty() || allStations.isEmpty()) return;

        log.info("Yo'lovchi simulyatsiyasi boshlandi. {} ta transport tekshirilmoqda...", activeTransports.size());

        for (Transport transport : activeTransports) {
            // Test rejimi: ehtimollikni 100% qilamiz (har intervalda hamma uchun yozadi)
            Station station = allStations.get(random.nextInt(allStations.size()));
            
            int capacity = transport.getCapacity() != null ? transport.getCapacity() : 50;
            int currentOnboard = transport.getCurrentPassengers() != null ? transport.getCurrentPassengers() : 0;
            
            int alighted = (int) (currentOnboard * (0.1 + random.nextDouble() * 0.4));
            if (alighted > currentOnboard) alighted = currentOnboard;
            currentOnboard -= alighted;
            
            int availableSpace = capacity - currentOnboard;
            int boarded = random.nextInt(Math.min(availableSpace, 15) + 1);
            currentOnboard += boarded;
            
            if (boarded == 0 && alighted == 0) continue;

            PassengerDto.UpdateRequest request = PassengerDto.UpdateRequest.builder()
                    .transportId(transport.getId())
                    .stationId(station.getId())
                    .boardedCount(boarded)
                    .alightedCount(alighted)
                    .totalOnboard(currentOnboard)
                    .recordedAt(LocalDateTime.now())
                    .build();

            try {
                passengerService.update(request);
                log.info("Simulyatsiya: {} transportga {} ta yo'lovchi chiqdi, {} ta tushdi. Jami: {}", 
                        transport.getPlateNumber(), boarded, alighted, currentOnboard);
            } catch (Exception e) {
                log.error("Yo'lovchi simulyatsiyasida xatolik: transport_id={}: {}", transport.getId(), e.getMessage());
            }
        }
    }
}
