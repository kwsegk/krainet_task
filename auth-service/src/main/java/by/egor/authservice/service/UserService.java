package by.egor.authservice.service;

import by.egor.authservice.dto.*;

import java.util.List;

public interface UserService {
    UserResponse register(RegisterRequest registerRequest);
    AuthResponse login(LoginRequest loginRequest);
    UserResponse getById(Long id);
    List<UserResponse> getAll();
    UserResponse update(Long id, UserUpdateRequest updateRequest);
    void delete(Long id);
}
