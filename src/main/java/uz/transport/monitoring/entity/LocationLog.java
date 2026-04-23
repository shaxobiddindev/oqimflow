package uz.transport.monitoring.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "location_logs",
        indexes = {
                @Index(name = "idx_location_transport", columnList = "transport_id"),
                @Index(name = "idx_location_timestamp", columnList = "recorded_at")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LocationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transport_id", nullable = false)
    private Transport transport;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column
    private Double speed; // km/h

    @Column(name = "passenger_count")
    private Integer passengerCount;

    @Column(name = "recorded_at", nullable = false)
    private LocalDateTime recordedAt;
}
