package uz.transport.monitoring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.transport.monitoring.entity.Route;

import java.util.List;
import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findAllByActiveTrue();
    Optional<Route> findByRouteNumberAndActiveTrue(String routeNumber);
    boolean existsByRouteNumber(String routeNumber);
}
