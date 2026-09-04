package com.gokul.auth_api.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/admin")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    @Operation(
            summary = "Access ADMIN endpoint",
            description = "Accessible only to authenticated users with the ADMIN role"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "ADMIN endpoint accessed successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    public String adminEndpoint() {
        return "Welcome ADMIN! You have access to this endpoint.";
    }
}