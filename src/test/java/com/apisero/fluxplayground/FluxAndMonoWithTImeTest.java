package com.apisero.fluxplayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoWithTImeTest {
    @Test
    public void infiniteSequence() throws InterruptedException {
       Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(100)).log();
       infiniteFlux.subscribe(element-> {
           System.out.println("value is : "+ element);
       });
       Thread.sleep(3000);
    }

    @Test
    public void finiteSequenceTest(){
        Flux<Long> finiteFlux = Flux.interval(Duration.ofMillis(100))
                .take(3)
                .log();
        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0L,1L,2L)
                .verifyComplete();
    }

    @Test
    public void finiteSequenceTestMap(){
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(100))
                .delayElements(Duration.ofSeconds(1))
                .map(l-> l.intValue())
                .take(3)
                .log();
        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0,1,2)
                .verifyComplete();
    }
}