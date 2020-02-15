package com.apisero.fluxplayground;


import org.junit.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMono {
	@Test
	public  void fluxTests() {
		Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
				//.concatWith(Flux.error(new RuntimeException("Exception Occurred")))
				.concatWith(Flux.just("After Error"))
				.log();
		stringFlux.subscribe(System.out::println, (e)-> System.out.println("Exception : "+e), () -> System.out.println("Completed"));
	}
	
	@Test
	public void fluxTestElements_withoutError() {
		Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring").log();
		StepVerifier.create(stringFlux)
		.expectNext("Spring")
		.expectNext("Spring Boot")
		.expectNext("Reactive Spring")
		.verifyComplete();
	}
	
	@Test
	public void fluxTestElements_withError() {
		Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
				.concatWith(Flux.error(new RuntimeException("Exception Occured")))
				.log();
		StepVerifier.create(stringFlux)
		.expectNext("Spring")
		.expectNext("Spring Boot")
		.expectNext("Reactive Spring")
//		.expectError(RuntimeException.class)
		.expectErrorMessage("Exception Occured")
		.verify();
	}

	@Test
	public void fluxTestElementsCount() {
		Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
				.concatWith(Flux.error(new RuntimeException("Exception Occured")))
				.log();
		StepVerifier.create(stringFlux)
				.expectNextCount(3)
				.expectError(RuntimeException.class)
				.verify();
	}

	@Test
	public void fluxTestElements_withError1() {
		Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
				.concatWith(Flux.error(new RuntimeException("Exception Occured")))
				.log();
		StepVerifier.create(stringFlux)
				.expectNext("Spring", "Spring Boot", "Reactive Spring")
//		.expectError(RuntimeException.class)
				.expectErrorMessage("Exception Occured")
				.verify();
	}

	@Test
	public void monoTest(){
		Mono<String> mono = Mono.just("Spring").log();
		StepVerifier.create(mono)
				.expectNext("Spring")
				.verifyComplete();
	}

	@Test
	public void monoTestError(){
		StepVerifier.create(Mono.error(new RuntimeException("Exception occured")).log())
				.expectError(RuntimeException.class)
				.verify();

	}
}
