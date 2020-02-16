package com.apisero.initialize;

import com.apisero.document.Item;
import com.apisero.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test")
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
    @Override
    public void run(String... args) throws Exception {
        initialDataSetup();
    }
    public  void initialDataSetup() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(itemReactiveRepository::save)
                .thenMany(itemReactiveRepository.findAll())
                .subscribe(item -> System.out.println("Item inserted from CommandLineRunner : "+item));
    }
}
