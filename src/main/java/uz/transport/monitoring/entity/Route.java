package uz.transport.monitoring.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "routes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Route extends BaseEntity {

    @Column(name = "route_number", nullable = false, unique = true, length = 20)
    private String routeNumber;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "start_point", length = 100)
    private String startPoint;

    @Column(name = "end_point", length = 100)
    private String endPoint;

    @Column(name = "distance_km")
    private Double distanceKm;

    @Column(name = "estimated_minutes")
    private Integer estimatedMinutes;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}
