package uz.transport.monitoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.transport.monitoring.entity.Station;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {
    List<Station> findAllByActiveTrue();

    @Query(value = """
            SELECT s.* FROM stations s
            WHERE s.active = true
              AND (6371 * acos(
                  cos(radians(:lat)) * cos(radians(s.latitude)) *
                  cos(radians(s.longitude) - radians(:lng)) +
                  sin(radians(:lat)) * sin(radians(s.latitude))
              )) < :radiusKm
            """, nativeQuery = true)
    List<Station> findNearbyStations(@Param("lat") Double lat,
                                     @Param("lng") Double lng,
                                     @Param("radiusKm") Double radiusKm);
}
