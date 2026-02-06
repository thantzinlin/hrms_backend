package com.hrms.controller;

import com.hrms.dto.JwtResponse;
import com.hrms.dto.LoginRequest;
import com.hrms.exception.ResourceNotFoundException;
import com.hrms.util.CustomApiResponse;
import com.hrms.model.Employee;
import com.hrms.model.User;
import com.hrms.repository.EmployeeRepository;
import com.hrms.repository.UserRepository;
import com.hrms.security.jwt.JwtUtils;
import com.hrms.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
        @Autowired
        AuthenticationManager authenticationManager;

        @Autowired
        UserRepository userRepository;

        @Autowired
        EmployeeRepository employeeRepository;

        @Autowired
        JwtUtils jwtUtils;

        @PostMapping("/signin")
        public ResponseEntity<CustomApiResponse<JwtResponse>> authenticateUser(
                        @Valid @RequestBody LoginRequest loginRequest) {

                try {
                        Authentication authentication = authenticationManager.authenticate(
                                        new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                                                        loginRequest.getPassword()));

                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        String jwt = jwtUtils.generateJwtToken(authentication);
                        String refreshToken = jwtUtils.generateRefreshToken(authentication);

                        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                        List<String> roles = userDetails.getAuthorities().stream()
                                        .map(item -> item.getAuthority())
                                        .collect(Collectors.toList());
                        Employee employee = employeeRepository.findByUser_UserId(userDetails.getUserId()).orElseThrow(
                                        () -> new ResourceNotFoundException("Error: Employee not found."));

                        JwtResponse jwtResponse = new JwtResponse(jwt, refreshToken, userDetails.getUserId(),
                                        userDetails.getUsername(), userDetails.getEmail(), roles,
                                        employee.getEmployeeId());
                        return ResponseEntity.ok(CustomApiResponse.<JwtResponse>builder().data(jwtResponse).build());
                } catch (AuthenticationException e) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                        .body(CustomApiResponse.<JwtResponse>builder()
                                                        .returnCode("401")
                                                        .returnMessage("Invalid username or password")
                                                        .data(null)
                                                        .build());
                }
        }

        @PostMapping("/refreshtoken")
        public ResponseEntity<CustomApiResponse<JwtResponse>> refreshtoken(
                        @RequestHeader("Authorization") String authorization) {
                String refreshToken = authorization.replace("Bearer ", "");
                if (jwtUtils.validateJwtToken(refreshToken)) {
                        String username = jwtUtils.getUserNameFromJwtToken(refreshToken);
                        User user = userRepository.findByUsername(username).get();

                        Authentication authentication = new UsernamePasswordAuthenticationToken(
                                        UserDetailsImpl.build(user), null, null);

                        String newAccessToken = jwtUtils.generateJwtToken(authentication);

                        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                        List<String> roles = userDetails.getAuthorities().stream()
                                        .map(item -> item.getAuthority())
                                        .collect(Collectors.toList());

                        Employee employee = employeeRepository.findByUser_UserId(userDetails.getUserId()).orElseThrow(
                                        () -> new ResourceNotFoundException("Error: Employee not found."));

                        JwtResponse jwtResponse = new JwtResponse(newAccessToken, refreshToken, userDetails.getUserId(),
                                        userDetails.getUsername(), userDetails.getEmail(), roles,
                                        employee.getEmployeeId());
                        return ResponseEntity.ok(CustomApiResponse.<JwtResponse>builder().data(jwtResponse).build());

                } else {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                        .body(CustomApiResponse.<JwtResponse>builder()
                                                        .returnCode("400")
                                                        .returnMessage("Error: Invalid refresh token!")
                                                        .data(null)
                                                        .build());
                }
        }
}
