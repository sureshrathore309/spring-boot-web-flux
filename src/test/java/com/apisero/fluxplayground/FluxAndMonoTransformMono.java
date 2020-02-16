package com.apisero.fluxplayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.io.Flushable;
import java.util.Arrays;
import java.util.List;

public class FluxAndMonoTransformMono {
    List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void transformUsingMap(){
        Flux<String> namesFlux = Flux.fromIterable(names).log()
                .map(s->s.toUpperCase());
        StepVerifier.create(namesFlux)
                .expectNext("ADAM", "ANNA", "JACK", "JENNY")
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Length(){
        Flux<Integer> namesFlux = Flux.fromIterable(names).log()
                .map(s->s.length());
        StepVerifier.create(namesFlux)
                .expectNext(4,4,4,5)
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Repeat(){
        Flux<Integer> namesFlux = Flux.fromIterable(names).log()
                .map(s->s.length())
                .repeat(1);
        StepVerifier.create(namesFlux)
                .expectNext(4,4,4,5,4,4,4,5)
                .verifyComplete();
    }

    @Test
    public void transformUsingMapFilter_Repeat(){
        Flux<String> namesFlux = Flux.fromIterable(names).log()
                .filter(s ->s.length()>4)
                .map(s->s.toUpperCase()).log();
        StepVerifier.create(namesFlux)
                .expectNext("JENNY")
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap() throws Exception{
        Flux<String> names = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))
                .flatMap(s->{
                    return Flux.fromIterable(convertToList(s));
                }).log(); //DB or external service call that return a flux

        StepVerifier.create(names)
                .expectNextCount(12)
                .verifyComplete();

    }

    @Test
    public void transformUsingFlatMapParallel() throws Exception{
        Flux<String> names = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))
                .window(2)
                .flatMap(s->
                   s.map(this::convertToList).subscribeOn(Schedulers.parallel())
                           .flatMap(s1 -> Flux.fromIterable(s1))
                ).log(); //DB or external service call that return a flux
        StepVerifier.create(names)
                .expectNextCount(12)
                .verifyComplete();

    }

    @Test
    public void transformUsingFlatConcatMapParallel() throws Exception{
        Flux<String> names = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))
                .window(2)
//                .flatMapSequential(s->
                .flatMap(s->
                                s.map(this::convertToList).subscribeOn(Schedulers.parallel())
                                .flatMap(s1 -> Flux.fromIterable(s1))
                ).log(); //DB or external service call that return a flux
        StepVerifier.create(names)
                .expectNextCount(12)
                .verifyComplete();

    }

    private List<String> convertToList(String s){
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s,"newValue");
    }

}
