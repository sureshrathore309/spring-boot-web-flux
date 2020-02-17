package com.apisero.initialize;

import com.apisero.document.Item;
import com.apisero.document.ItemCapped;
import com.apisero.repository.ItemCappedReactiveRepository;
import com.apisero.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test")
@Slf4j
public class ItemDataInitializer implements CommandLineRunner {

    List<Item> itemList = Arrays.asList(
            new Item(null, "Samsumng TV", 400.0),
            new Item(null, "Poco F1", 400.0),
            new Item(null, "LG TV", 410.0),
            new Item(null, "Apple TV", 1400.0),
            new Item(null, "Iphone", 1400.0),
            new Item("ABC", "Boss Headphones", 209.0));

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Autowired
    ItemCappedReactiveRepository itemCappedReactiveRepository;

    @Autowired
    MongoOperations mongoOperations;
    
    @Override
    public void run(String... args) throws Exception {
        initialDataSetup();
        createCappedCollection();
        dataSetUpForCappedCollection();
    }

    private void createCappedCollection() {
        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped());
    }

    public  void initialDataSetup() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemReactiveRepository::save)
                .thenMany(itemReactiveRepository.findAll())
                .subscribe(item -> System.out.println("Item inserted from CommandLineRunner : "+item));
    }

    public void dataSetUpForCappedCollection(){
       Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
                .map(i -> new ItemCapped(null, "Radom Item", 100.00 +i));
       itemCappedReactiveRepository.insert(itemCappedFlux)
               .subscribe(itemCapped ->
                       log.info("Item is inserted from commandLineRunner : "+ itemCapped));
    }
}
