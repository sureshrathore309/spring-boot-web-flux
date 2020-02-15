package com.apisero.fluxplayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxMonoFactory {

    List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");
    @Test
    public void fluxUsingIterable(){
       Flux<String> namesFlux =  Flux.fromIterable(names).log();
        StepVerifier.create(namesFlux)
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void fluxUsingArray(){
        String[] names = new String[]{"adam", "anna", "jack", "jenny"};
        Flux<String> namesFlux = Flux.fromArray(names);
        StepVerifier.create(namesFlux).expectNext("adam", "anna", "jack", "jenny")
        .verifyComplete();
    }

    @Test
    public void fluxUsingStream(){
        Flux<String> namesFlux = Flux.fromStream(names.stream()).log();
        StepVerifier.create(namesFlux)
                .expectNext("adam", "anna", "jack", "jenny")
                .verifyComplete();
    }

    @Test
    public void monoUsingJustOrEmpty(){
       Mono<String> mono = Mono.justOrEmpty(null); //Mono.Empty()
        StepVerifier.create(mono.log()).verifyComplete();
    }




}
