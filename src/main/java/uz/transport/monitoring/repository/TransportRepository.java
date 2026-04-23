package uz.transport.monitoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.transport.monitoring.entity.Transport;
import uz.transport.monitoring.enums.TransportStatus;

import java.util.List;
import java.util.Optional;

public interface TransportRepository extends JpaRepository<Transport, Long> {

    List<Transport> findAllByActiveTrue();

    List<Transport> findAllByStatusAndActiveTrue(TransportStatus status);

    List<Transport> findAllByRouteIdAndActiveTrue(Long routeId);

    Optional<Transport> findByPlateNumberAndActiveTrue(String plateNumber);

    Optional<Transport> findByDeviceIdAndActiveTrue(String deviceId);

    @Query("SELECT t FROM Transport t WHERE t.active = true AND t.status = 'ACTIVE'")
    List<Transport> findAllActiveMoving();

    @Query("SELECT COUNT(t) FROM Transport t WHERE t.status = :status AND t.active = true")
    long countByStatus(@Param("status") TransportStatus status);
}
