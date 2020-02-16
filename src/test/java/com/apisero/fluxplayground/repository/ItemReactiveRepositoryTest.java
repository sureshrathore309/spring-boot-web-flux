package com.apisero.fluxplayground.repository;

import com.apisero.document.Item;
import com.apisero.repository.ItemReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@RunWith(SpringRunner.class)
@DirtiesContext
public class ItemReactiveRepositoryTest {
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
    public void getAllItems(){
       Flux<Item> itemFlux = itemReactiveRepository.findAll();
        StepVerifier.create(itemFlux)
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void getItemById(){
        StepVerifier.create(itemReactiveRepository.findById("ABC"))
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("Boss Headphones"))
                .verifyComplete();
    }

    @Test
    public void findItemByDescription(){
        StepVerifier.create(itemReactiveRepository.findByDescription("Apple TV").log())
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveItem(){
        Item item = new Item(null, "Gooogle Home", 29.00);
        Mono<Item> savedItem = itemReactiveRepository.save(item);
        StepVerifier.create(savedItem)
                .expectSubscription()
                .expectNextMatches(item1 -> item1.getDescription().equals("Gooogle Home") && item1.getId() != null)
                .verifyComplete();
    }

    @Test
    public void updateItem(){
        double newPrice = 199.0;
        Mono<Item> updatedItem=itemReactiveRepository.findByDescription("LG TV")
                .map(item -> {
                    item.setPrice(newPrice);
                    return item;
                }).flatMap(item -> itemReactiveRepository.save(item));
        StepVerifier.create(updatedItem)
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice() == 199.0)
                .verifyComplete();
    }

    @Test
    public void deleteItemById(){
        Mono<Void> deleteItem = itemReactiveRepository.findById("ABC") //Mono of item
                .map(Item::getId).flatMap(id -> itemReactiveRepository.deleteById(id));
        StepVerifier.create(deleteItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void delteItem(){
        Mono<Void> deleteItem = itemReactiveRepository.findByDescription("LG TV")
                .flatMap(item -> itemReactiveRepository.delete(item));
        StepVerifier.create(deleteItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }
}