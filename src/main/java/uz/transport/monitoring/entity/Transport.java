package uz.transport.monitoring.entity;

import jakarta.persistence.*;
import lombok.*;
import uz.transport.monitoring.enums.TransportStatus;
import uz.transport.monitoring.enums.TransportType;

@Entity
@Table(name = "transports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transport extends BaseEntity {

    @Column(name = "plate_number", nullable = false, unique = true, length = 20)
    private String plateNumber;

    @Column(nullable = false, length = 100)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransportType type;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TransportStatus status = TransportStatus.OFFLINE;

    // Oxirgi ma'lum joylashuv
    @Column(name = "last_latitude")
    private Double lastLatitude;

    @Column(name = "last_longitude")
    private Double lastLongitude;

    @Column(name = "current_passengers")
    @Builder.Default
    private Integer currentPassengers = 0;

    @Column(name = "device_id", unique = true, length = 64)
    private String deviceId;

    @Column(nullable = false)
    @Builder.Default
    private boolean active = true;
}
