package com.stub.stub1.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WebClientService {

    private final WebClient webClient;

    public WebClientService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://jsonplaceholder.typicode.com").build();
    }

    public Mono<String> getPostByIdAsync(int postId) {

        System.out.println("Executing on thread: " + Thread.currentThread().getName());
        return this.webClient
                .get()
                .uri("/posts/{id}", postId)  
                .retrieve()  
                .bodyToMono(String.class)  
                .doOnNext(response -> System.out.println("Response received: " + response)) 
                .doOnError(error -> System.err.println("Error occurred: " + error.getMessage())); 
    }
}
