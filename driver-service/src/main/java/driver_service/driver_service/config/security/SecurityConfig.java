package driver_service.driver_service.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-resources/**",
                                "/actuator/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(grantedAuthoritiesExtractor())
                                .decoder(kafkaAwareJwtDecoder())
                        )
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    private Converter<Jwt, AbstractAuthenticationToken> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakRealmRoleConverter());
        return converter;
    }

    static class KeycloakRealmRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {
        public Collection<GrantedAuthority> convert(Jwt jwt) {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess == null) return Collections.emptyList();

            return ((List<String>) realmAccess.get("roles")).stream()
                    .map(role -> "ROLE_" + role.toUpperCase())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        }
    }

    @Bean
    public JwtDecoder kafkaAwareJwtDecoder() {
        return new CustomKafkaJwtDecoder();
    }

    static class CustomKafkaJwtDecoder implements JwtDecoder {
        private final NimbusJwtDecoder jwtDecoder =
                NimbusJwtDecoder.withJwkSetUri("http://keycloak:8181/realms/internm/protocol/openid-connect/certs")
                        .build();

        @Override
        public Jwt decode(String token) throws JwtException {
            try {
                return jwtDecoder.decode(token);
            } catch (JwtException e) {
                OAuth2Error error = new OAuth2Error(
                        "invalid_token",
                        "Invalid token from Kafka: " + e.getMessage(),
                        null
                );

                throw new JwtValidationException(
                        "Invalid Kafka message token",
                        Collections.singletonList(error)
                );
            }
        }
    }
}