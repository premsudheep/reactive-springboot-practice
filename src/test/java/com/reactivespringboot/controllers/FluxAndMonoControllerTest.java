package com.reactivespringboot.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@WebFluxTest // Reads on @Controller, @RestController etc.
public class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void fluxApproach1() {
        Flux<Integer> integerFlux = webTestClient
                .get()
                .uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier
                .create(integerFlux)
                .expectSubscription()
                .expectNext(1,2,3,4)
                .verifyComplete();
    }

    @Test
    public void fluxApproach2() {
        webTestClient
                .get()
                .uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Integer.class)
                .hasSize(4);
    }

    @Test
    public void fluxApproach3() {
        List<Integer> expected = Arrays.asList(1,2,3,4);
        EntityExchangeResult<List<Integer>> result = webTestClient
                .get()
                .uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .returnResult();

        assertEquals(expected, result.getResponseBody());
    }

    @Test
    public void fluxApproach4() {
        List<Integer> expected = Arrays.asList(1,2,3,4);
        webTestClient
                .get()
                .uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .consumeWith((response) -> assertEquals(expected, response.getResponseBody())
        );
    }

    @Test
    public void fluxStreamInfinite() {
        Flux<Long> longFluxInfiniteStream = webTestClient
                .get()
                .uri("/fluxInfiniteStream")
                .accept(MediaType.valueOf(MediaType.APPLICATION_STREAM_JSON_VALUE))
                .exchange()
                .expectStatus().isOk()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier
                .create(longFluxInfiniteStream)
                .expectNext(0L)
                .expectNext(1L)
                .expectNext(2L)
                .thenCancel()
                .verify();
    }

    @Test
    public void testMono() {
        webTestClient
                .get()
                .uri("/mono")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith(response -> assertEquals(response.getResponseBody(), new Integer(1)));
    }
}
