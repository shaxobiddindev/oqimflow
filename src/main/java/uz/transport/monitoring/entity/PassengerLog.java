package uz.transport.monitoring.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "passenger_logs",
        indexes = {
                @Index(name = "idx_passenger_transport", columnList = "transport_id"),
                @Index(name = "idx_passenger_station", columnList = "station_id"),
                @Index(name = "idx_passenger_time", columnList = "recorded_at")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassengerLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transport_id", nullable = false)
    private Transport transport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    private Station station;

    @Column(name = "boarded_count", nullable = false)
    @Builder.Default
    private Integer boardedCount = 0;

    @Column(name = "alighted_count", nullable = false)
    @Builder.Default
    private Integer alightedCount = 0;

    @Column(name = "total_onboard", nullable = false)
    private Integer totalOnboard;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;
}
