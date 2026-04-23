package uz.transport.monitoring.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uz.transport.monitoring.dto.AnalyticsDto;
import uz.transport.monitoring.service.AnalyticsService;
import uz.transport.monitoring.websocket.LocationPublisher;

@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardBroadcastScheduler {

    private final AnalyticsService analyticsService;
    private final LocationPublisher locationPublisher;

    /**
     * Har 15 sekundda dashboard statistikasini yangilash
     */
    @Scheduled(fixedRate = 15000)
    public void broadcastDashboard() {
        try {
            AnalyticsDto stats = analyticsService.getDashboard();
            locationPublisher.broadcastDashboardStats(stats);
        } catch (Exception e) {
            log.error("Dashboard broadcast xatosi: {}", e.getMessage());
        }
    }
}
