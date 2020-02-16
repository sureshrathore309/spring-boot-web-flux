package com.apisero.fluxplayground.handler;

import com.apisero.constants.ItemConstants;
import com.apisero.document.Item;
import com.apisero.repository.ItemReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static com.apisero.constants.ItemConstants.ITEM_END_POINT_V1;
import static com.apisero.constants.ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemHandlerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> itemList = Arrays.asList(
            new Item(null, "Samsumng TV", 400.0),
            new Item(null, "LG TV", 410.0),
            new Item(null, "Apple TV", 1400.0),
            new Item(null, "Iphone", 1400.0),
            new Item("ABC", "Boss Headphones", 209.0));

    @Before
    public void setUp(){
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> {
                    System.out.println("Inserted Item is : " + item);
                })
                .blockLast();
    }

    @Test
    public void GetAllItems(){
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5);
    }

    @Test
    public void getItemById(){
        webTestClient.get().uri(ITEM_FUNCTIONAL_END_POINT_V1.concat("/ABC"))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("ABC");

    }



    @Test
    public void createItemTest(){
        Item item = new Item(null, "Xiami", 1009.00);
        webTestClient.post().uri(ITEM_FUNCTIONAL_END_POINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("Xiami")
                .jsonPath("$.price").isEqualTo(1009.00);
    }

    @Test
    public void updateItem(){
        double newPrice = 1999.0;
        Item item = new Item("ABC", "Vivo v11", newPrice);
        webTestClient.put().uri(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"),"ABC")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item),Item.class)
                .exchange()
                .expectBody()
                .jsonPath("$.price", newPrice);
    }

    @Test
    public void deletItemTest(){
        webTestClient.delete().uri(ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"),"ABC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Void.class);
    }
}
