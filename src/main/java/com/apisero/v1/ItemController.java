package com.apisero.v1;

import static com.apisero.constants.ItemConstants.*;
import com.apisero.document.Item;
import com.apisero.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ItemController {
    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @GetMapping(ITEM_END_POINT_V1)
    public Flux<Item> getAllItems(){
      return itemReactiveRepository.findAll();
    }

    @GetMapping(ITEM_END_POINT_V1+"/{id}")
    public Mono<ResponseEntity<Item>> getOneItem(@PathVariable("id") String id){
        return itemReactiveRepository.findById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(ITEM_END_POINT_V1)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody Item item){
        return itemReactiveRepository.save(item);
    }

    @DeleteMapping(ITEM_END_POINT_V1+"/{id}")
    public Mono<Void> deleteItem(@PathVariable("id") String id){
        return itemReactiveRepository.deleteById(id);
    }

    @PutMapping(ITEM_END_POINT_V1+"/{id}")
    public Mono<ResponseEntity<Item>> updateItem(@PathVariable("id") String id ,@RequestBody Item item){
       return itemReactiveRepository.findById(id).flatMap(currentItem -> {
            currentItem.setPrice(item.getPrice());
            currentItem.setDescription(item.getDescription());
            return itemReactiveRepository.save(currentItem);
        }).map(updateItem -> new ResponseEntity<>(updateItem, HttpStatus.OK))
        .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(ITEM_END_POINT_V1+"/runtimeException")
    public Flux<Item> runtimeException(){
        return itemReactiveRepository.findAll().concatWith(Mono.error(new RuntimeException("Exception Occured")));
    }
}
