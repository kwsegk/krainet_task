package by.egor.authservice.service;

import by.egor.authservice.exception.AccessDeniedException;
import by.egor.authservice.exception.BadRequestException;
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
    private final NotificationClient notificationClient;

    private List<String> getAdminEmails() {
        return userRepository.findAll()
                .stream()
                .filter(u -> u.getRole() == Role.ADMIN)
                .map(User::getEmail)
                .toList();
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest registerRequest) {
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new ConflictException("Username is already in use");
        }

        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new ConflictException("Email is already in use");
        }

        String rawPassword = registerRequest.getPassword();

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

        if (savedUser.getRole() == Role.USER) {
            notificationClient.notifyUserCreated(
                    savedUser.getUsername(),
                    rawPassword,
                    savedUser.getEmail(),
                    getAdminEmails()
            );
        }

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

        if (!user.getUsername().equals(updateRequest.getUsername()) &&
                userRepository.findByUsername(updateRequest.getUsername()).isPresent()) {
            throw new ConflictException("Username is already in use");
        }

        if (!user.getEmail().equals(updateRequest.getEmail()) &&
                userRepository.findByEmail(updateRequest.getEmail()).isPresent()) {
            throw new ConflictException("Email is already in use");
        }

        String oldUsername = user.getUsername();
        String oldEmail = user.getEmail();
        String oldFirstName = user.getFirstName();
        String oldLastName = user.getLastName();
        user.setUsername(updateRequest.getUsername());
        user.setFirstName(updateRequest.getFirstName());
        user.setLastName(updateRequest.getLastName());
        user.setEmail(updateRequest.getEmail());

        User updatedUser = userRepository.save(user);
        log.info("User updated: {}", updatedUser.getUsername());

        if (currentUser.getRole() == Role.USER) {
            notificationClient.notifyUserUpdated(
                    oldUsername, updatedUser.getUsername(),
                    oldEmail, updatedUser.getEmail(),
                    oldFirstName, updatedUser.getFirstName(),
                    oldLastName, updatedUser.getLastName(),
                    getAdminEmails()
            );
        }

        return toResponse(updatedUser);
    }

    @Override
    @Transactional
    public void changePassword(Long id, ChangePasswordRequest request) {
        log.info("Changing password for user with id: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        User currentUser = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        if (currentUser.getRole() == Role.USER && !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("You can only change your own password");
        }

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }

        String oldPassword = request.getOldPassword();
        String newPassword = request.getNewPassword();

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password changed for user: {}", user.getUsername());

        if (currentUser.getRole() == Role.USER) {
            notificationClient.notifyPasswordChanged(
                    user.getUsername(),
                    user.getEmail(),
                    oldPassword,
                    newPassword,
                    getAdminEmails()
            );
        }
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        log.info("Deleting user with id: {}", id);

        User currentUser = (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        if (currentUser.getRole() == Role.USER && !currentUser.getId().equals(id)) {
            throw new AccessDeniedException("You can only delete your own account");
        }

        refreshTokenService.deleteByUser(user);

        if (currentUser.getRole() == Role.USER) {
            notificationClient.notifyUserDeleted(
                    user.getUsername(),
                    user.getEmail(),
                    getAdminEmails()
            );
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
