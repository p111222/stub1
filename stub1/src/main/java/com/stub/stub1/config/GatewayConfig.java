package com.stub.stub1.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("resttemplate_route", r -> r.path("/firstRestapi/**")
                        .and()
                        .header("Source", "RestTemplate")
                        .and()
                        .method(HttpMethod.POST)
                        .filters(f -> f.addRequestHeader("client_id", "restapi")
                                .addRequestHeader("client_secret", "ALBuL7PIiq9Rz08eC62VrJypRYeuvenu"))
                        .uri("http://43.204.108.73:8344/firstRestapi"))

                .route("webclient_route", r -> r.path("/secondRestapi/**")
                        .and()
                        .header("Source", "WebClient")
                        .and()
                        .method(HttpMethod.POST)
                        .uri("http://43.204.108.73:8344/secondRestapi"))
                .build();
    }
}
