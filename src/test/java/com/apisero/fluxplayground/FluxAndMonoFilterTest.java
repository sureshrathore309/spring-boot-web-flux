package com.apisero.fluxplayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

public class FluxAndMonoFilterTest {
    List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void filterTest(){
       Flux<String>  stringFlux= Flux.fromIterable(names).log()
               .filter(s->s.length()>4);
        StepVerifier.create(stringFlux)
                .expectNextCount(1)
                .verifyComplete();
    }
}
