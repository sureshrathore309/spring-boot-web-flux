package com.apisero.fluxplayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoErrorTest {
    @Test
    public void fluxErrorHandling(){
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occured")))
                .concatWith(Flux.just("D"));
        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A","B", "C")
                .expectError(RuntimeException.class)
                .verify();
    }

    @Test
    public void fluxErrorMap(){
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occured")))
                .concatWith(Flux.just("D"))
                .onErrorMap((e)-> new CustomException(e));
        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A","B", "C")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    public void fluxErrorMapWithRetry(){
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occured")))
                .concatWith(Flux.just("D"))
                .onErrorMap((e)-> new CustomException(e))
                .retry(2);
        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A","B", "C")
                .expectNext("A","B", "C")
                .expectNext("A","B", "C")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    public void fluxErrorMapWithRetryBackOf(){
        Flux<String> stringFlux = Flux.just("A", "B", "C")
                .concatWith(Flux.error(new RuntimeException("Exception Occured")))
                .concatWith(Flux.just("D"))
                .onErrorMap((e)-> new CustomException(e))
                .retryBackoff(2, Duration.ofSeconds(5));
        StepVerifier.create(stringFlux.log())
                .expectSubscription()
                .expectNext("A","B", "C")
                .expectNext("A","B", "C")
                .expectNext("A","B", "C")
                .expectError(IllegalStateException.class)
                .verify();
    }
}
