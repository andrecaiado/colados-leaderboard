package com.example.colados_leaderboard_api.controller;

import com.example.colados_leaderboard_api.dto.JwtResponseDto;
import com.example.colados_leaderboard_api.dto.LoginRequestDto;
import com.example.colados_leaderboard_api.entity.AppUser;
import com.example.colados_leaderboard_api.exceptions.EntityNotFound;
import com.example.colados_leaderboard_api.security.JwtUtil;
import com.example.colados_leaderboard_api.service.AppUserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final AppUserService appUserService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, ClientRegistrationRepository clientRegistrationRepository, AppUserService appUserService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.appUserService = appUserService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponseDto> login(@RequestBody @Valid LoginRequestDto loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
        );

        final UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String accessToken = jwtUtil.generateToken(userDetails);
        JwtResponseDto jwtResponse = JwtResponseDto.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .build();

        return ResponseEntity.ok(jwtResponse);
    }

    @GetMapping("/google")
    public void initiateGoogleLogin(HttpServletResponse response) throws IOException {

        ClientRegistration google = clientRegistrationRepository.findByRegistrationId("google");

        String authUrl = google.getProviderDetails().getAuthorizationUri()
                + "?client_id=" + google.getClientId()
                + "&redirect_uri=" + URLEncoder.encode(google.getRedirectUri(), StandardCharsets.UTF_8)
                + "&response_type=code"
                + "&scope=openid%20email%20profile";

        response.sendRedirect(authUrl);
    }

    @GetMapping("/google/callback")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam String code) {
        ClientRegistration google = clientRegistrationRepository.findByRegistrationId("google");

        RestTemplate restTemplate = new RestTemplate();

        // Exchange code for token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", google.getClientId());
        params.add("client_secret", google.getClientSecret());
        params.add("redirect_uri", google.getRedirectUri());
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<Map<String, Object>> tokenResponse = restTemplate.exchange(
                google.getProviderDetails().getTokenUri(),
                HttpMethod.POST,
                tokenRequest,
                new ParameterizedTypeReference<>() {
                }
        );

        Map<String, Object> tokenBody = tokenResponse.getBody();
        if (tokenBody == null || !tokenBody.containsKey("access_token")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to retrieve access token from Google");
        }
        String accessToken = (String) tokenResponse.getBody().get("access_token");

        // Get user info
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(accessToken);

        HttpEntity<String> userRequest = new HttpEntity<>(userHeaders);

        ResponseEntity<Map<String, Object>> userInfoResponse = restTemplate.exchange(
                google.getProviderDetails().getUserInfoEndpoint().getUri(),
                HttpMethod.GET,
                userRequest,
                new ParameterizedTypeReference<>() {}
        );

        Map<String, Object> userInfo = userInfoResponse.getBody();
        if (userInfo == null || !userInfo.containsKey("email")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to retrieve user information from Google");
        }
        String email = (String) userInfo.get("email");

        // Validate if the email is authorized
        AppUser appUser;
        try {
            appUser = appUserService.getByEmail(email);
        } catch (EntityNotFound e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Email not authorized");
        }

        String localAccessToken = jwtUtil.generateTokenForOAuth(appUser);
        JwtResponseDto jwtResponse = JwtResponseDto.builder()
                .accessToken(localAccessToken)
                .tokenType("Bearer")
                .build();

        return ResponseEntity.ok(jwtResponse);
    }
}
