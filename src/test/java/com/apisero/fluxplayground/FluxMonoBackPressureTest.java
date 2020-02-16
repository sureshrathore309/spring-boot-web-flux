package com.apisero.fluxplayground;

import org.junit.Test;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxMonoBackPressureTest {

    @Test
    public void backPressureTest(){
        Flux<Integer> finiteFlux = Flux.range(1,10).log();
        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .thenRequest(1)
                .expectNext(1)
                .thenRequest(1)
                .expectNext(2)
                .thenCancel()
                .verify();
    }

    @Test
    public void backPressure(){
        Flux<Integer> finiteFlux = Flux.range(1,10).log();
        finiteFlux.subscribe(e -> System.out.println("Element is : "+e),
                (e)-> System.out.println("Exeption is : "+e),
                ()-> System.out.println("Done"),
                (subscription -> subscription.request(2)));
     }

    @Test
    public void backPressureCancel(){
        Flux<Integer> finiteFlux = Flux.range(1,10).log();
        finiteFlux.subscribe(e -> System.out.println("Element is : "+e),
                (e)-> System.out.println("Exeption is : "+e),
                ()-> System.out.println("Done"),
                (subscription -> subscription.cancel()));
    }

    @Test
    public void customizebackPressure(){
        Flux<Integer> finiteFlux = Flux.range(1,10).log();
        finiteFlux.subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnNext(Integer value) {
                request(1);
                System.out.println("value recieved is : " + value);
                if (value == 4)
                    cancel();
            }
        });
    }

}