package com.hospital.management.service;

import com.hospital.management.dto.ApiResponse;
import com.hospital.management.dto.LoginRequest;
import com.hospital.management.dto.LoginResponse;
import com.hospital.management.dto.SignupRequest;
import com.hospital.management.entity.Role;
import com.hospital.management.entity.User;
import com.hospital.management.enums.RoleName;
import com.hospital.management.exception.ResourceNotFoundException;
import com.hospital.management.repository.RoleRepository;
import com.hospital.management.repository.UserRepository;
import com.hospital.management.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);

        return new LoginResponse(jwt, "Bearer");
    }

    public ApiResponse signup(SignupRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return new ApiResponse(false, "Username is already taken!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return new ApiResponse(false, "Email address is already in use!");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Set<Role> roles = new HashSet<>();

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            roles = request.getRoles().stream()
                    .map(roleName -> {
                        String formattedRole = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;
                        return roleRepository.findByName(RoleName.valueOf(formattedRole))
                                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", roleName));
                    })
                    .collect(Collectors.toSet());
        } else {
            Role defaultRole = roleRepository.findByName(RoleName.ROLE_RECEPTIONIST)
                    .orElseThrow(() -> new ResourceNotFoundException("Role", "name", "ROLE_RECEPTIONIST"));
            roles = Collections.singleton(defaultRole);
        }

        user.setRoles(roles);
        userRepository.save(user);

        return new ApiResponse(true, "User registered successfully!");
    }
}
