package com.apisero.handler;

import com.apisero.document.Item;
import com.apisero.document.ItemCapped;
import com.apisero.repository.ItemCappedReactiveRepository;
import com.apisero.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.*;

@Component
public class ItemHandler {
    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Autowired
    ItemCappedReactiveRepository itemCappedReactiveRepository;

    static Mono<ServerResponse> notFound = ServerResponse.notFound().build();

    public Mono<ServerResponse> getAllItem(ServerRequest serverRequest){
       return ServerResponse.ok().
                contentType(APPLICATION_JSON)
                .body(itemReactiveRepository.findAll(), Item.class);
    }

    public Mono<ServerResponse> getOneItem(ServerRequest serverRequest){
       String id = serverRequest.pathVariable("id");
       Mono<Item> itemMono = itemReactiveRepository.findById(id);
       return itemMono.flatMap(item -> ServerResponse.ok()
               .contentType(APPLICATION_JSON)
               .body(BodyInserters.fromObject(item)).switchIfEmpty(notFound));
    }

    public Mono<ServerResponse> createItem(ServerRequest serverRequest){
       Mono<Item> itemMono = serverRequest.bodyToMono(Item.class);
       return itemMono.flatMap(item -> ServerResponse.ok()
       .contentType(APPLICATION_JSON)
       .body(itemReactiveRepository.save(item), Item.class));
    }

    public Mono<ServerResponse> deleteItem(ServerRequest serverRequest){
       String id =  serverRequest.pathVariable("id");
       Mono<Void> deleteItem = itemReactiveRepository.deleteById(id);
       return ServerResponse.ok()
               .contentType(APPLICATION_JSON)
               .body(deleteItem, Item.class);
    }

    public Mono<ServerResponse> updateItem(ServerRequest serverRequest){
        String id =serverRequest.pathVariable("id");
        Mono<Item> updatedItem = serverRequest.bodyToMono(Item.class)
                .flatMap(item -> {
                   Mono<Item> itemMono= itemReactiveRepository.findById(id)
                            .flatMap(currentItem -> {
                                    currentItem.setDescription(item.getDescription());
                                    currentItem.setPrice(item.getPrice());
                                    return itemReactiveRepository.save(currentItem);
                            }) ;
                    return itemMono;
                });
        return updatedItem.flatMap(item -> ServerResponse.ok()
        .contentType(APPLICATION_JSON)
                .body(BodyInserters.fromObject(item)).switchIfEmpty(notFound));
    }

    public Mono<ServerResponse> itemsEx(ServerRequest serverRequest){
       throw new RuntimeException("RuntimeException occured");
    }

    public Mono<ServerResponse> itemsStream(ServerRequest serverRequest) {
        return ServerResponse.ok()
                .contentType(APPLICATION_STREAM_JSON)
                .body(itemCappedReactiveRepository.findItemsBy(), ItemCapped.class);
    }
}