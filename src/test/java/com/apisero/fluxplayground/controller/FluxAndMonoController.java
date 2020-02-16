package com.apisero.fluxplayground.controller;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@WebFluxTest
@DirtiesContext
public class FluxAndMonoController {

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void fluxApproach1(){
       Flux<Integer> integerFlux = webTestClient.get()
                .uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Integer.class).getResponseBody();

        StepVerifier.create(integerFlux)
                .expectSubscription()
                .expectNext(1,2,3,4)
                .verifyComplete();
    }

    @Test
    public void fluxApproach2(){
        webTestClient.get()
                .uri("/fluxStream")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_STREAM_JSON_VALUE)
                .expectBodyList(Integer.class)
                .hasSize(4);
    }

    @Test
    public void fluxApproach3(){
       List<Integer> integerList = Arrays.asList(1,2,3,4);
       EntityExchangeResult<List<Integer>> entityExchangeResult= webTestClient.get()
        .uri("/flux")
        .accept(MediaType.APPLICATION_JSON)
        .exchange()
        .expectBodyList(Integer.class)
        .returnResult();
        Assert.assertEquals(integerList, entityExchangeResult.getResponseBody());
    }

    @Test
    public void fluxApproach4(){
        List<Integer> integerList = Arrays.asList(1,2,3,4);
        webTestClient.get()
                .uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBodyList(Integer.class)
                .consumeWith(response -> Assert.assertEquals(integerList, response.getResponseBody()));
    }

    @Test
    public void fluxStream(){
        Flux<Long> longFlux = webTestClient.get()
                .uri("/fluxStream")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(longFlux)
                .expectSubscription()
                .expectNext(0l)
                .expectNext(1l)
                .expectNext(2l)
                .thenCancel()
                .verify();
    }

    @Test
    public void monoTest(){
        webTestClient.get().uri("/mono")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith(response -> {
                    Assert.assertEquals(response.getResponseBody(), new Integer(1));
                });
    }
}
