package uz.transport.monitoring.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.transport.monitoring.dto.LocationDto;
import uz.transport.monitoring.entity.Transport;
import uz.transport.monitoring.repository.TransportRepository;
import uz.transport.monitoring.service.LocationService;
import uz.transport.monitoring.websocket.LocationPublisher;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * GPS Simulyator - Toshkent koordinatalari asosida
 * real transport harakatini simulyatsiya qiladi
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GpsSimulatorScheduler {

    private final TransportRepository transportRepository;
    private final LocationService locationService;
    private final LocationPublisher locationPublisher;

    private final Random random = new Random();

    // Toshkent markaziy koordinatalari
    private static final double BASE_LAT = 41.2995;
    private static final double BASE_LNG = 69.2401;
    private static final double SPREAD = 0.05; // ~5 km radius

    @Scheduled(fixedRateString = "${app.scheduler.gps-interval:5000}")
    public void simulateGps() {
        List<Transport> activeTransports = transportRepository.findAllByActiveTrue();
        if (activeTransports.isEmpty()) return;

        List<LocationDto.Response> updates = new ArrayList<>();

        for (Transport transport : activeTransports) {
            // Oldingi koordinatadan biroz siljish
            double baseLat = transport.getLastLatitude() != null
                    ? transport.getLastLatitude()
                    : BASE_LAT + (random.nextDouble() - 0.5) * SPREAD;
            double baseLng = transport.getLastLongitude() != null
                    ? transport.getLastLongitude()
                    : BASE_LNG + (random.nextDouble() - 0.5) * SPREAD;

            // Kichik harakatlanish (har 5 sekundda ~50-100 metr)
            double deltaLat = (random.nextDouble() - 0.5) * 0.001;
            double deltaLng = (random.nextDouble() - 0.5) * 0.001;

            double newLat = baseLat + deltaLat;
            double newLng = baseLng + deltaLng;

            // Toshkent chegarasi ichida ushlab turish
            newLat = Math.max(41.25, Math.min(41.35, newLat));
            newLng = Math.max(69.20, Math.min(69.35, newLng));

            double speed = 10 + random.nextDouble() * 50; // 10-60 km/h

            // Yo'lovchi soni simulyatsiyasi (vaqtga qarab)
            int hour = LocalDateTime.now().getHour();
            int passengers = simulatePassengerCount(transport.getCapacity(), hour);

            LocationDto.UpdateRequest req = LocationDto.UpdateRequest.builder()
                    .transportId(transport.getId())
                    .latitude(newLat)
                    .longitude(newLng)
                    .speed(Math.round(speed * 10.0) / 10.0)
                    .passengerCount(passengers)
                    .recordedAt(LocalDateTime.now())
                    .build();

            try {
                LocationDto.Response response = locationService.updateLocation(req);
                updates.add(response);
            } catch (Exception e) {
                log.error("GPS simulyatsiya xatosi transport {}: {}", transport.getId(), e.getMessage());
            }
        }

        // Barcha yangilashlarni bir broadcast bilan yuborish
        if (!updates.isEmpty()) {
            locationPublisher.broadcastLocations(updates);
            log.debug("GPS simulyatsiya: {} transport yangilandi", updates.size());
        }
    }

    /**
     * Vaqtga qarab yo'lovchi soni simulyatsiyasi
     * Tong va kech saatlarda ko'p, tunda kam
     */
    private int simulatePassengerCount(int capacity, int hour) {
        double loadFactor;
        if (hour >= 7 && hour <= 9) {
            loadFactor = 0.7 + random.nextDouble() * 0.3; // 70-100% - Tong chog'i
        } else if (hour >= 17 && hour <= 19) {
            loadFactor = 0.6 + random.nextDouble() * 0.4; // 60-100% - Kechki chog'
        } else if (hour >= 10 && hour <= 16) {
            loadFactor = 0.3 + random.nextDouble() * 0.4; // 30-70% - Kunduz
        } else if (hour >= 20 && hour <= 22) {
            loadFactor = 0.2 + random.nextDouble() * 0.3; // 20-50% - Kechqurun
        } else {
            loadFactor = 0.05 + random.nextDouble() * 0.15; // 5-20% - Tun
        }
        return (int) (capacity * loadFactor);
    }
}
