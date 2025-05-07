package taxi.config;


import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import taxi.dto.RegistrationRequest;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class KeycloakUtil {

    private final Environment env;
    private final RestTemplate restTemplate;

    public String getAdminAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "password");
        params.add("client_id", "admin-cli");
        params.add("username", env.getProperty("keycloak.admin-username"));
        params.add("password", env.getProperty("keycloak.admin-password"));

        String url = env.getProperty("keycloak.auth-server-url") + "/realms/master/protocol/openid-connect/token";

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(params, headers),
                Map.class
        );

        return (String) response.getBody().get("access_token");
    }

    public void createUser(String adminToken, RegistrationRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        Map<String, Object> credentials = new HashMap<>();
        credentials.put("type", "password");
        credentials.put("value", request.getPassword());
        credentials.put("temporary", false);

        Map<String, Object> user = new HashMap<>();
        user.put("username", request.getUsername());
        user.put("email", request.getEmail());
        user.put("firstName", request.getFirstName());
        user.put("lastName", request.getLastName());
        user.put("enabled", true);
        user.put("credentials", List.of(credentials));

        String url = env.getProperty("keycloak.auth-server-url") +
                "/admin/realms/" + env.getProperty("keycloak.realm") + "/users";

        restTemplate.exchange(
                url,
                HttpMethod.POST,
                new HttpEntity<>(user, headers),
                Void.class
        );
    }

    public String getTokenUri() {
        return env.getProperty("keycloak.auth-server-url") +
                "/realms/" + env.getProperty("keycloak.realm") +
                "/protocol/openid-connect/token";
    }
}
