package com.gokul.auth_api.controller;

import com.gokul.auth_api.dto.UserResponse;
import com.gokul.auth_api.model.User;
import com.gokul.auth_api.repository.UserRepository;
import org.springframework.web.bind.annotation.PutMapping;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import com.gokul.auth_api.dto.UpdateUserRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final UserRepository userRepository;

    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // =========================
    // Admin API
    // =========================

    @Operation(
            summary = "Access ADMIN endpoint",
            description = "Accessible only to authenticated users with the ADMIN role"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "ADMIN endpoint accessed successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            )
    })
    @GetMapping
    public String adminEndpoint() {

        return "Welcome ADMIN! You have access to this endpoint.";
    }

    // =========================
    // Get all users
    // =========================

    @Operation(
            summary = "Get all users",
            description = "Returns all registered users without exposing passwords"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Users retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            )
    })
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {

        List<UserResponse> users =
                userRepository.findAll()
                        .stream()
                        .map(this::convertToUserResponse)
                        .toList();

        return ResponseEntity.ok(users);
    }

    // =========================
    // Delete user
    // =========================

    @Operation(
            summary = "Delete a user",
            description = "Deletes a registered user by ID"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "User deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(
            @PathVariable Long id) {

        // Check whether user exists
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        // Delete user
        userRepository.deleteById(id);

        // Return 204 No Content
        return ResponseEntity.noContent().build();
    }

    // =========================
// Update user
// =========================

    @Operation(
            summary = "Update a user",
            description = "Updates the username, email, and role of a registered user"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation failed"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Authentication required"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Access denied"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found"
            )
    })
    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @org.springframework.web.bind.annotation.RequestBody UpdateUserRequest request) {

        User user = userRepository.findById(id)
                .orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setRole(request.getRole());

        User updatedUser = userRepository.save(user);

        return ResponseEntity.ok(
                convertToUserResponse(updatedUser)
        );
    }

    // =========================
    // Convert User to DTO
    // =========================

    private UserResponse convertToUserResponse(User user) {

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }
}