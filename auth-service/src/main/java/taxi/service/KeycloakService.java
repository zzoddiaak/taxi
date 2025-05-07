package taxi.service;


import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import taxi.config.KeycloakUtil;
import taxi.dto.AccessTokenResponse;
import taxi.dto.LoginRequest;
import taxi.dto.LogoutResponse;
import taxi.dto.RegistrationRequest;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final Environment env;
    private final RestTemplate restTemplate;
    private final KeycloakUtil keycloakUtil;

    public AccessTokenResponse authenticate(LoginRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", env.getProperty("keycloak.resource"));
        params.add("client_secret", env.getProperty("keycloak.credentials.secret"));
        params.add("username", request.getUsername());
        params.add("password", request.getPassword());

        ResponseEntity<AccessTokenResponse> response = restTemplate.exchange(
                keycloakUtil.getTokenUri(),
                HttpMethod.POST,
                new HttpEntity<>(params, headers),
                AccessTokenResponse.class
        );

        return response.getBody();
    }

    public AccessTokenResponse refreshToken(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", env.getProperty("keycloak.resource"));
        params.add("client_secret", env.getProperty("keycloak.credentials.secret"));
        params.add("refresh_token", refreshToken);

        ResponseEntity<AccessTokenResponse> response = restTemplate.exchange(
                keycloakUtil.getTokenUri(),
                HttpMethod.POST,
                new HttpEntity<>(params, headers),
                AccessTokenResponse.class
        );

        return response.getBody();
    }

    public ResponseEntity<?> registerUser(RegistrationRequest request) {
        try {
            String adminToken = keycloakUtil.getAdminAccessToken();
            keycloakUtil.createUser(adminToken, request);
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Registration failed: " + e.getMessage());
        }
    }

    public ResponseEntity<?> logout(String refreshToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", env.getProperty("keycloak.resource"));
        params.add("client_secret", env.getProperty("keycloak.credentials.secret"));
        params.add("refresh_token", refreshToken);

        String url = env.getProperty("keycloak.auth-server-url") +
                "/realms/" + env.getProperty("keycloak.realm") +
                "/protocol/openid-connect/logout";

        try {
            restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(params, headers),
                    Void.class
            );
            return ResponseEntity.ok(new LogoutResponse("Logged out successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new LogoutResponse("Logout failed: " + e.getMessage()));
        }
    }
}