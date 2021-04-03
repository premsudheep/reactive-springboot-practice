package com.reactivespringboot.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {

    @Test
    public void fluxTest() {
        Flux<String> stringFlux = Flux
                .just("prem", "sudheep", "springboot")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))  //throwing an exception
                .concatWith(Flux.just("After Error")) // this will not be called after exception
                .log(); //this will log all the events behind the scene when a subscribe is called
        stringFlux
                .subscribe(System.out::println, // onNext()
                        System.err::println,    // onError()
                        () -> System.out.println("Completed")); // onComplete()
    }

    @Test
    public void fluxTestElementsWithoutError() {
        Flux<String> stringFlux = Flux
                .just("prem", "sudheep", "springboot")
                .log();
        StepVerifier.create(stringFlux)
                .expectNext("prem")
                .expectNext("sudheep")
                .expectNext("springboot")
                .verifyComplete(); // subscriber
    }

    @Test
    public void fluxTestElementsWithError() {
        Flux<String> stringFlux = Flux
                .just("prem", "sudheep", "springboot")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();
        StepVerifier.create(stringFlux)
                .expectNext("prem")
                .expectNext("sudheep")
                .expectNext("springboot")
                .expectError(RuntimeException.class)
                .verify(); // subscriber
    }

    @Test
    public void fluxTestElementsCountWithError() {
        Flux<String> stringFlux = Flux
                .just("prem", "sudheep", "springboot")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();
        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .expectError(RuntimeException.class)
                .verify(); // subscriber
    }

    @Test
    public void fluxTestElementsWithError1() {
        Flux<String> stringFlux = Flux
                .just("prem", "sudheep", "springboot")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();
        StepVerifier.create(stringFlux)
                .expectNext("prem", "sudheep", "springboot")
                .expectError(RuntimeException.class)
                .verify(); // subscriber
    }

    @Test
    public void monoTest() {
        Mono<String> stringMono = Mono.just("prem");

        StepVerifier.create(stringMono.log())
                .expectNext("prem")
                .verifyComplete();
    }


    @Test
    public void monoTestError() {

        StepVerifier.create(Mono.error(new RuntimeException("Exception Occurred")))
                .expectError(RuntimeException.class)
                .verify();

        StepVerifier.create(Mono.error(new RuntimeException("Exception Occurred")))
                .expectErrorMessage("Exception Occurred")
                .verify();
    }
}
