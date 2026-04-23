package uz.transport.monitoring.websocket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import uz.transport.monitoring.dto.LocationDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Barcha transport joylashuvlarini broadcast qilish
     * Client: /topic/locations ga subscribe bo'ladi
     */
    public void broadcastLocations(List<LocationDto.Response> locations) {
        messagingTemplate.convertAndSend("/topic/locations", locations);
        log.debug("Broadcast {} transport locations", locations.size());
    }

    /**
     * Bitta transport yangilanishi
     * Client: /topic/transport/{id} ga subscribe bo'ladi
     */
    public void sendTransportUpdate(Long transportId, LocationDto.Response location) {
        messagingTemplate.convertAndSend("/topic/transport/" + transportId, location);
    }

    /**
     * Dashboard statistikasi yangilanishi
     */
    public void broadcastDashboardStats(Object stats) {
        messagingTemplate.convertAndSend("/topic/dashboard", stats);
    }
}
