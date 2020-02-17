package com.apisero.repository;

import com.apisero.document.ItemCapped;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ItemCappedReactiveRepository extends ReactiveMongoRepository<ItemCapped, String> {
    @Tailable
    Flux<ItemCapped> findItemsBy();
}
