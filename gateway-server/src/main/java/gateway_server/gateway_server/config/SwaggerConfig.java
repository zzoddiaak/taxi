package gateway_server.gateway_server.config;


import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {



    @Bean
    @Lazy(false)
    public List<GroupedOpenApi> apis(SwaggerUiConfigParameters swaggerUiConfigParameters,
                                     RouteDefinitionLocator locator) {
        List<GroupedOpenApi> groups = new ArrayList<>();

        groups.add(GroupedOpenApi.builder()
                .pathsToMatch("/api/**")
                .group("Gateway")
                .build());

        List<RouteDefinition> definitions = locator.getRouteDefinitions().collectList().block();
        definitions.stream()
                .filter(route -> route.getId().matches(".*-service"))
                .forEach(route -> {
                    String name = route.getId();
                    String path = route.getPredicates().stream()
                            .filter(predicate -> predicate.getName().equals("Path"))
                            .findFirst()
                            .map(predicate -> predicate.getArgs().get("pattern"))
                            .orElse("/" + name + "/**");

                    groups.add(GroupedOpenApi.builder()
                            .pathsToMatch(path.replace("/**", ""))
                            .group(name)
                            .build());
                });

        return groups;
    }


    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Taxi Service API Gateway")
                        .version("1.0")
                        .description("Unified API Gateway for all Taxi Services"))
                .externalDocs(new ExternalDocumentation()
                        .description("Eureka Dashboard")
                        .url("http://localhost:8761"));
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.addExposedHeader("*");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}