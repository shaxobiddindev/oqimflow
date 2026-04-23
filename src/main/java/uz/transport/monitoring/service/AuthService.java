package uz.transport.monitoring.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.transport.monitoring.dto.AuthDto;
import uz.transport.monitoring.entity.User;
import uz.transport.monitoring.enums.UserRole;
import uz.transport.monitoring.exception.BusinessException;
import uz.transport.monitoring.repository.UserRepository;
import uz.transport.monitoring.util.JwtUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthDto.TokenResponse login(AuthDto.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        User user = userRepository.findByUsernameAndActiveTrue(request.getUsername())
                .orElseThrow(() -> new BusinessException("Foydalanuvchi topilmadi"));

        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        log.info("User logged in: {}", request.getUsername());

        return new AuthDto.TokenResponse(
                accessToken, refreshToken,
                jwtUtil.getExpiration(),
                user.getUsername(),
                user.getRole().name()
        );
    }

    @Transactional
    public AuthDto.TokenResponse register(AuthDto.RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Bu username allaqachon band: " + request.getUsername());
        }

        UserRole role = request.getRole() != null ? request.getRole() : UserRole.ROLE_VIEWER;

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .role(role)
                .active(true)
                .build();

        userRepository.save(user);
        log.info("New user registered: {}", request.getUsername());

        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return new AuthDto.TokenResponse(
                accessToken, refreshToken,
                jwtUtil.getExpiration(),
                user.getUsername(),
                user.getRole().name()
        );
    }

    public AuthDto.TokenResponse refresh(AuthDto.RefreshRequest request) {
        String username = jwtUtil.extractUsername(request.getRefreshToken());
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (!jwtUtil.isTokenValid(request.getRefreshToken(), userDetails)) {
            throw new BusinessException("Refresh token yaroqsiz yoki muddati o'tgan");
        }

        User user = userRepository.findByUsernameAndActiveTrue(username)
                .orElseThrow(() -> new BusinessException("Foydalanuvchi topilmadi"));

        String newAccessToken = jwtUtil.generateToken(userDetails);
        String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

        return new AuthDto.TokenResponse(
                newAccessToken, newRefreshToken,
                jwtUtil.getExpiration(),
                user.getUsername(),
                user.getRole().name()
        );
    }
}
