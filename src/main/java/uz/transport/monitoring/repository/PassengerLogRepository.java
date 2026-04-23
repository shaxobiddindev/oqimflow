package uz.transport.monitoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.transport.monitoring.entity.PassengerLog;

import java.time.LocalDateTime;
import java.util.List;

public interface PassengerLogRepository extends JpaRepository<PassengerLog, Long> {

    List<PassengerLog> findByTransportIdAndRecordedAtBetweenOrderByRecordedAtDesc(
            Long transportId, LocalDateTime from, LocalDateTime to);

    @Query("SELECT SUM(p.boardedCount) FROM PassengerLog p " +
            "WHERE p.transport.id = :transportId AND p.recordedAt >= :from")
    Long totalBoardedByTransport(@Param("transportId") Long transportId,
                                  @Param("from") LocalDateTime from);

    @Query("SELECT SUM(p.boardedCount) FROM PassengerLog p WHERE p.station.id = :stationId")
    Long totalBoardedByStation(@Param("stationId") Long stationId);

    @Query("SELECT p.station.id, p.station.name, SUM(p.boardedCount) as total, " +
            "AVG(CASE WHEN t.capacity > 0 THEN (p.totalOnboard * 100.0) / t.capacity ELSE 0.0 END) as avgLoad " +
            "FROM PassengerLog p JOIN p.transport t WHERE p.station IS NOT NULL " +
            "AND p.recordedAt >= :from GROUP BY p.station.id, p.station.name " +
            "ORDER BY SUM(p.boardedCount) DESC")
    List<Object[]> topStationsByBoardings(@Param("from") LocalDateTime from);

    @Query("SELECT p.station.id, p.station.name, AVG(p.totalOnboard) as avgLoad " +
            "FROM PassengerLog p WHERE p.station IS NOT NULL " +
            "AND p.recordedAt >= :from GROUP BY p.station.id, p.station.name " +
            "ORDER BY avgLoad DESC")
    List<Object[]> stationsByAvgLoad(@Param("from") LocalDateTime from);
}
