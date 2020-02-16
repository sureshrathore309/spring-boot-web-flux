package com.apisero.fluxplayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

import java.time.Duration;

public class FluxAndMonoCombineTest {
    @Test
    public void combineUsingMerge(){
        Flux<String> flux1 = Flux.just("A","B","C");
        Flux<String> flux2 = Flux.just("D", "E","F");
        Flux<String> mergeFlux = Flux.merge(flux1,flux2);
        StepVerifier.create(mergeFlux)
                .expectSubscription()
                .expectNext("A","B","C","D","E","F")
                .verifyComplete();
    }

    @Test
    public void combineUsingMergeWithDelay(){
        VirtualTimeScheduler.getOrSet();
        Flux<String> flux1 = Flux.just("A","B","C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E","F").delayElements(Duration.ofSeconds(1));
        Flux<String> mergeFlux = Flux.merge(flux1,flux2).log();
        StepVerifier
                .withVirtualTime(()-> mergeFlux.log())
                .expectSubscription()
                .thenAwait(Duration.ofSeconds(3))
                .expectNextCount(6)
             //   .expectNext("A","B","C","D","E","F")
                .verifyComplete();
    }

    @Test
    public void combineUsingConcat(){
        Flux<String> flux1 = Flux.just("A","B","C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D", "E","F").delayElements(Duration.ofSeconds(1));
        Flux<String> mergeFlux = Flux.concat(flux1,flux2).log();
        StepVerifier.create(mergeFlux)
                .expectSubscription()
                .expectNext("A","B","C","D","E","F")
                .verifyComplete();
    }

    @Test
    public void combineUsingConcatWithDelay(){
        Flux<String> flux1 = Flux.just("A","B","C");
        Flux<String> flux2 = Flux.just("D", "E","F");
        Flux<String> mergeFlux = Flux.concat(flux1,flux2).log();
        StepVerifier.create(mergeFlux)
                .expectSubscription()
                .expectNext("A","B","C","D","E","F")
                .verifyComplete();
    }

    @Test
    public void combineUsingZip(){
        Flux<String> flux1 = Flux.just("A","B","C");
        Flux<String> flux2 = Flux.just("D", "E","F");
        Flux<String> mergeFlux = Flux.zip(flux1,flux2,(f1,f2) -> {
            return f1.concat(f2);
        }).log(); //(A,D),(E,E)
        StepVerifier.create(mergeFlux)
                .expectSubscription()
                .expectNext("A","B","C","D","E","F")
                .verifyComplete();
    }
}