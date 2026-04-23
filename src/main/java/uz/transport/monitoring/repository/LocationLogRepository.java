package uz.transport.monitoring.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.transport.monitoring.entity.LocationLog;

import java.time.LocalDateTime;
import java.util.List;

public interface LocationLogRepository extends JpaRepository<LocationLog, Long> {

    List<LocationLog> findByTransportIdAndRecordedAtBetweenOrderByRecordedAtDesc(
            Long transportId, LocalDateTime from, LocalDateTime to);

    @Query("SELECT l FROM LocationLog l WHERE l.transport.id = :transportId " +
            "ORDER BY l.recordedAt DESC")
    List<LocationLog> findLatestByTransportId(@Param("transportId") Long transportId, Pageable pageable);

    @Query("SELECT AVG(l.passengerCount) FROM LocationLog l " +
            "WHERE l.transport.id = :transportId AND l.recordedAt >= :from")
    Double avgPassengerCount(@Param("transportId") Long transportId,
                             @Param("from") LocalDateTime from);

    @Query("SELECT HOUR(l.recordedAt) as hour, AVG(l.passengerCount) as avgPassengers " +
            "FROM LocationLog l WHERE l.recordedAt >= :from " +
            "GROUP BY HOUR(l.recordedAt) ORDER BY hour")
    List<Object[]> getHourlyAvgPassengers(@Param("from") LocalDateTime from);

    void deleteByRecordedAtBefore(LocalDateTime before);
}
