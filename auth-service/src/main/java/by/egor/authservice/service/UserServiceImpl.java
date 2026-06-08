package by.egor.authservice.service;

import by.egor.authservice.exception.AccessDeniedException;
import by.egor.authservice.exception.ConflictException;
import by.egor.authservice.exception.ResourceNotFoundException;
import by.egor.authservice.repository.UserRepository;
import by.egor.authservice.dto.*;
import by.egor.authservice.entity.Role;
import by.egor.authservice.entity.User;
import by.egor.authservice.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new ConflictException("Username is already in use");
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered: {}", savedUser.getUsername());

        return toResponse(savedUser);
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String accessToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user).getToken();
        log.info("User logged in: {}", user.getUsername());

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        log.info("Fetching user by id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        User currentUser = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        if (currentUser.getRole() == Role.USER && !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("You can only view your own account");
        }

        return toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        log.info("Fetching all users");

        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse update(Long id, UserUpdateRequest updateRequest) {
        log.info("Updating user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        User currentUser = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        if (currentUser.getRole() == Role.USER && !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("You can only update your own account");
        }

        user.setUsername(updateRequest.getUsername());
        user.setFirstName(updateRequest.getFirstName());
        user.setLastName(updateRequest.getLastName());
        user.setEmail(updateRequest.getEmail());

        User updatedUser = userRepository.save(user);
        log.info("User updated: {}", updatedUser.getUsername());

        return toResponse(updatedUser);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        log.info("Deleting user with id: {}", id);

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }

        User currentUser = (User) SecurityContextHolder.getContext()
                        .getAuthentication().getPrincipal();

        if (currentUser.getRole() == Role.USER && !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("You can only delete your own account");
        }

        userRepository.deleteById(id);
        log.info("User deleted with id: {}", id);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole().name()
        );
    }

    @Override
    public AuthResponse refresh(RefreshRequest refreshRequest) {
        String newAccessToken = refreshTokenService.refreshAccessToken(refreshRequest.getRefreshToken());
        return new AuthResponse(newAccessToken, refreshRequest.getRefreshToken());
    }

    @Override
    public void logout(RefreshRequest refreshRequest) {
        refreshTokenService.deleteByToken(refreshRequest.getRefreshToken());
        log.info("User logged out");
    }
}
