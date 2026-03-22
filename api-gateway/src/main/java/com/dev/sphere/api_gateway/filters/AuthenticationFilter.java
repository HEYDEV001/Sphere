package com.dev.sphere.api_gateway.filters;

import com.dev.sphere.api_gateway.service.JwtService;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.config> {

    private final JwtService jwtService;


    public AuthenticationFilter(JwtService jwtService) {
        super(config.class);
        this.jwtService = jwtService;
    }
    @Override
    public GatewayFilter apply(config config) {
        return (exchange, chain) ->{
            log.info("login Request {}", exchange.getRequest().getURI());
            final String tokenHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                log.info("Authorization header not found");
                return exchange.getResponse().setComplete();
            }

            final String token = tokenHeader.split("Bearer ")[1];

            try{
                String userId = jwtService.getIdFromTheToken(token);

                ServerWebExchange modifiedExchange = exchange
                        .mutate()
                        .request(req->req.header("userId", userId))
                        .build();


                return chain.filter(modifiedExchange);
            }catch (JwtException e) {
                log.error("Jwt Exception {}", e.getLocalizedMessage());
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

                return exchange.getResponse().setComplete();
            }

        } ;
    }



    public static class config{

    }
}
