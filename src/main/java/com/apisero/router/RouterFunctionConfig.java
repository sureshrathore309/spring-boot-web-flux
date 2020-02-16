package com.apisero.router;

import com.apisero.handler.SampleHandlerFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.awt.*;

import static org.springframework.web.reactive.function.server.RequestPredicates.accept;


@Configuration
public class RouterFunctionConfig<S> {
    @Bean
    public RouterFunction<ServerResponse> rout(SampleHandlerFunction handlerFunction){
        return RouterFunctions
                .route(RequestPredicates.GET("/function/flux").and(accept(MediaType.APPLICATION_JSON)), handlerFunction::flux)
                .andRoute(RequestPredicates.GET("/function/mono").and(accept(MediaType.APPLICATION_JSON)), handlerFunction::mono);
    }
}
