package uz.transport.monitoring.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import uz.transport.monitoring.entity.Route;
import uz.transport.monitoring.entity.Station;
import uz.transport.monitoring.entity.Transport;
import uz.transport.monitoring.entity.User;
import uz.transport.monitoring.enums.TransportStatus;
import uz.transport.monitoring.enums.TransportType;
import uz.transport.monitoring.enums.UserRole;
import uz.transport.monitoring.repository.RouteRepository;
import uz.transport.monitoring.repository.StationRepository;
import uz.transport.monitoring.repository.TransportRepository;
import uz.transport.monitoring.repository.UserRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RouteRepository routeRepository;
    private final StationRepository stationRepository;
    private final TransportRepository transportRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Boshlang'ich ma'lumotlarni tekshirish...");

        if (userRepository.count() == 0) {
            seedUsers();
        }

        if (routeRepository.count() == 0) {
            seedRoutes();
        }

        if (stationRepository.count() == 0) {
            seedStations();
        }

        if (transportRepository.count() == 0) {
            seedTransports();
        }

        log.info("Boshlang'ich ma'lumotlar holati tekshirildi.");
    }

    private void seedUsers() {
        List<User> users = List.of(
                User.builder().username("admin").password(passwordEncoder.encode("admin123"))
                        .fullName("Admin User").role(UserRole.ROLE_ADMIN).active(true).build(),
                User.builder().username("operator").password(passwordEncoder.encode("operator123"))
                        .fullName("Operator Ism").role(UserRole.ROLE_OPERATOR).active(true).build(),
                User.builder().username("viewer").password(passwordEncoder.encode("viewer123"))
                        .fullName("Ko'ruvchi Ism").role(UserRole.ROLE_VIEWER).active(true).build()
        );
        userRepository.saveAll(users);
        log.info("Foydalanuvchilar yaratildi: admin / operator / viewer");
    }

    private void seedRoutes() {
        List<Route> routes = List.of(
                Route.builder().routeNumber("27").name("Yunusobod → Chilonzor")
                        .startPoint("Yunusobod").endPoint("Chilonzor")
                        .distanceKm(18.5).estimatedMinutes(55).active(true).build(),
                Route.builder().routeNumber("67").name("Sergeli → Olmazor")
                        .startPoint("Sergeli").endPoint("Olmazor")
                        .distanceKm(22.0).estimatedMinutes(65).active(true).build(),
                Route.builder().routeNumber("45").name("Mirzo Ulug'bek → Bektemir")
                        .startPoint("Mirzo Ulug'bek").endPoint("Bektemir")
                        .distanceKm(15.0).estimatedMinutes(45).active(true).build(),
                Route.builder().routeNumber("33").name("Shayhontohur → Yakkasaroy")
                        .startPoint("Shayhontohur").endPoint("Yakkasaroy")
                        .distanceKm(10.0).estimatedMinutes(35).active(true).build()
        );
        routeRepository.saveAll(routes);
    }

    private void seedStations() {
        List<Station> stations = List.of(
                Station.builder().name("Mustaqillik maydoni").latitude(41.2995).longitude(69.2401)
                        .address("Mustaqillik ko'chasi, 1").active(true).build(),
                Station.builder().name("Amir Temur xiyoboni").latitude(41.3041).longitude(69.2793)
                        .address("Amir Temur ko'chasi, 108").active(true).build(),
                Station.builder().name("Chorsu bozori").latitude(41.3269).longitude(69.2329)
                        .address("Chorsu ko'chasi").active(true).build(),
                Station.builder().name("Yunusobod savdo markazi").latitude(41.3602).longitude(69.2928)
                        .address("Yunusobod ko'chasi, 5").active(true).build(),
                Station.builder().name("Chilonzor 9").latitude(41.2887).longitude(69.2012)
                        .address("Chilonzor ko'chasi, 9").active(true).build(),
                Station.builder().name("Alisher Navoiy metrost.").latitude(41.2961).longitude(69.2714)
                        .address("Navoiy ko'chasi").active(true).build(),
                Station.builder().name("Beruniy metrost.").latitude(41.3102).longitude(69.2463)
                        .address("Beruniy ko'chasi").active(true).build(),
                Station.builder().name("Sergeli turar joy massivi").latitude(41.2543).longitude(69.2214)
                        .address("Sergeli ko'chasi, 12").active(true).build()
        );
        stationRepository.saveAll(stations);
    }

    private void seedTransports() {
        List<Route> routes = routeRepository.findAll();
        if (routes.isEmpty()) return;

        List<Transport> transports = List.of(
                // Marshrut 27
                Transport.builder().plateNumber("01 A 777 AA").model("Mercedes-Benz Citaro")
                        .type(TransportType.BUS).capacity(80).route(routes.get(0))
                        .status(TransportStatus.ACTIVE).currentPassengers(0)
                        .lastLatitude(41.3002).lastLongitude(69.2450)
                        .deviceId("DEV-001").active(true).build(),
                Transport.builder().plateNumber("01 B 222 BB").model("MAN Lion's City")
                        .type(TransportType.BUS).capacity(90).route(routes.get(0))
                        .status(TransportStatus.ACTIVE).currentPassengers(0)
                        .lastLatitude(41.3120).lastLongitude(69.2600)
                        .deviceId("DEV-002").active(true).build(),
                // Marshrut 67
                Transport.builder().plateNumber("01 C 333 CC").model("Isuzu Turkuaz")
                        .type(TransportType.MINIBUS).capacity(25).route(routes.get(1))
                        .status(TransportStatus.ACTIVE).currentPassengers(0)
                        .lastLatitude(41.2700).lastLongitude(69.2200)
                        .deviceId("DEV-003").active(true).build(),
                Transport.builder().plateNumber("01 D 444 DD").model("Isuzu Turkuaz")
                        .type(TransportType.MINIBUS).capacity(25).route(routes.get(1))
                        .status(TransportStatus.IDLE).currentPassengers(0)
                        .lastLatitude(41.2800).lastLongitude(69.2300)
                        .deviceId("DEV-004").active(true).build(),
                // Marshrut 45
                Transport.builder().plateNumber("01 E 555 EE").model("DAEWOO BS106")
                        .type(TransportType.BUS).capacity(70).route(routes.get(2))
                        .status(TransportStatus.ACTIVE).currentPassengers(0)
                        .lastLatitude(41.3200).lastLongitude(69.2700)
                        .deviceId("DEV-005").active(true).build(),
                // Marshrut 33
                Transport.builder().plateNumber("01 F 666 FF").model("YuTong ZK6118")
                        .type(TransportType.BUS).capacity(100).route(routes.get(3))
                        .status(TransportStatus.ACTIVE).currentPassengers(0)
                        .lastLatitude(41.3050).lastLongitude(69.2550)
                        .deviceId("DEV-006").active(true).build(),
                // Biriktirilmagan (zaxira)
                Transport.builder().plateNumber("01 G 888 GG").model("Hyundai Aero City")
                        .type(TransportType.BUS).capacity(60)
                        .status(TransportStatus.OFFLINE).currentPassengers(0)
                        .deviceId("DEV-007").active(true).build()
        );
        transportRepository.saveAll(transports);
        log.info("{} ta transport yaratildi", transports.size());
    }
}
