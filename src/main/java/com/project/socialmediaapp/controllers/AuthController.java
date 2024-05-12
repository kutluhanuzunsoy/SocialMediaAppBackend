package com.project.socialmediaapp.controllers;

import com.project.socialmediaapp.entities.RefreshToken;
import com.project.socialmediaapp.entities.User;
import com.project.socialmediaapp.requests.RefreshRequest;
import com.project.socialmediaapp.requests.user_requests.UserLoginRequest;
import com.project.socialmediaapp.requests.user_requests.UserRegisterRequest;
import com.project.socialmediaapp.responses.AuthResponse;
import com.project.socialmediaapp.security.JWTTokenProvider;
import com.project.socialmediaapp.services.RefreshTokenService;
import com.project.socialmediaapp.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JWTTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager, JWTTokenProvider jwtTokenProvider, UserService userService, PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody UserLoginRequest userLoginRequest) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userLoginRequest.getUserName(), userLoginRequest.getPassword());
        Authentication auth = authenticationManager.authenticate(authToken);
        User user = userService.getUserByUsername(userLoginRequest.getUserName());
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt = "Bearer " + jwtTokenProvider.generateJWTToken(auth);

        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(jwt);
        authResponse.setRefreshToken(refreshTokenService.createRefreshToken(user));
        authResponse.setUserId(user.getId());

        return authResponse;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody UserRegisterRequest userRegisterRequest) {
        AuthResponse authResponse = new AuthResponse();

        if (userService.getUserByUsername(userRegisterRequest.getUserName()) != null) {
            authResponse.setMessage("Username is already taken");
            return new ResponseEntity<>(authResponse, HttpStatus.BAD_REQUEST).getBody();
        }
        User newUser = new User();
        newUser.setUserName(userRegisterRequest.getUserName());
        newUser.setPassword(passwordEncoder.encode(userRegisterRequest.getPassword()));
        userService.saveUser(newUser);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userRegisterRequest.getUserName(), userRegisterRequest.getPassword());
        Authentication auth = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt = "Bearer " + jwtTokenProvider.generateJWTToken(auth);

        authResponse.setMessage("User registered successfully");
        authResponse.setAccessToken(jwt);
        authResponse.setRefreshToken(refreshTokenService.createRefreshToken(newUser));
        authResponse.setUserId(newUser.getId());

        return new ResponseEntity<>(authResponse, HttpStatus.CREATED).getBody();
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest refreshRequest) {
        AuthResponse authResponse = new AuthResponse();
        RefreshToken refreshToken = refreshTokenService.getRefreshTokenByUserId(refreshRequest.getUserId());

        if (refreshToken.getToken().equals(refreshRequest.getRefreshToken()) && !refreshTokenService.isRefreshTokenExpired(refreshToken)) {
            User user = refreshToken.getUser();
            String jwt = "Bearer " + jwtTokenProvider.generateJWTTokenByUserId(user.getId());

            authResponse.setMessage("Token refreshed successfully");
            authResponse.setAccessToken(jwt);
            authResponse.setUserId(user.getId());

            return new ResponseEntity<>(authResponse, HttpStatus.OK);

        } else {
            authResponse.setMessage("Invalid refresh token");
            return new ResponseEntity<>(authResponse, HttpStatus.UNAUTHORIZED);
        }
    }

}