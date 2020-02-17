package com.apisero.router;

import static com.apisero.constants.ItemConstants.*;
import com.apisero.handler.ItemHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration("ItemRouter")
public class ItemRouter {

    @Bean
    public RouterFunction<ServerResponse> itemRouter(ItemHandler itemHandler){
        return RouterFunctions.route(GET(ITEM_FUNCTIONAL_END_POINT_V1)
        .and(accept(MediaType.APPLICATION_JSON)), itemHandler::getAllItem)
                .andRoute(GET(ITEM_FUNCTIONAL_END_POINT_V1+"/{id}")
                        .and(accept(MediaType.APPLICATION_JSON)), itemHandler::getOneItem)
                .andRoute(POST(ITEM_FUNCTIONAL_END_POINT_V1)
                        .and(accept(MediaType.APPLICATION_JSON)), itemHandler::createItem)
                .andRoute(DELETE(ITEM_FUNCTIONAL_END_POINT_V1+"/{id}")
                        .and(accept(MediaType.APPLICATION_JSON)), itemHandler::deleteItem)
                .andRoute(PUT(ITEM_FUNCTIONAL_END_POINT_V1+"/{id}")
                        .and(accept(MediaType.APPLICATION_JSON)), itemHandler::updateItem);
    }

    @Bean
    public RouterFunction<ServerResponse> errorRoute(ItemHandler itemHandler){
       return RouterFunctions.route(GET("fun/runtimeException")
                .and(accept(MediaType.APPLICATION_JSON)), itemHandler::itemsEx);
    }

    @Bean
    public RouterFunction<ServerResponse> itemStreamRoute(ItemHandler itemHandler){
        return RouterFunctions.route(GET(ITEM_STREAM_FUNCTIONAL_END_POINT_V1)
                .and(accept(MediaType.APPLICATION_JSON)), itemHandler::itemsStream);
    }
}

