package com.apisero.fluxplayground.v1;

import com.apisero.constants.ItemConstants;
import com.apisero.document.ItemCapped;
import com.apisero.repository.ItemCappedReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
@Slf4j
public class ItemStreamControllerTest {
    @Autowired
    ItemCappedReactiveRepository itemCappedReactiveRepository;

    @Autowired
    MongoOperations mongoOperations;

    @Autowired
    WebTestClient webTestClient;

    @Before
    public void setUp(){
        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped());
        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
                .map(i -> new ItemCapped(null, "Radom Item", 100.00 +i)).take(5);
        itemCappedReactiveRepository.insert(itemCappedFlux)
                .doOnNext(itemCapped -> System.out.println("Inserted Intem is setUp : "+itemCapped)).blockLast();
    }

    @Test
    public void testStreamAllItems(){
        Flux<ItemCapped> itemCappedFlux = webTestClient.get().uri(ItemConstants.ITEM_STREAM_END_POINT_V1)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(ItemCapped.class)
                .getResponseBody()
                .take(5);

        StepVerifier.create(itemCappedFlux)
                .expectNextCount(5)
                .thenCancel()
                .verify();
    }
}
