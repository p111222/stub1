package com.stub.stub1.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class Service2 {

    private final WebClient webClient = WebClient.builder().build();

    @KafkaListener(topics = "service2-topic", groupId = "group_id")
    public void consumeFromKafka(String message) {
        String path = determinePathFromMessage(message);
        String gatewayUrl = "http://localhost:8081/" + path;
        System.out.println("service2topic"+gatewayUrl);

        webClient.post()
                .uri(gatewayUrl)
                .header("Message", message) 
                .bodyValue(message)
                .retrieve() 
                .bodyToMono(String.class)
                .doOnNext(response -> System.out.println("Response: " + response))
                .subscribe();
    }

    private String determinePathFromMessage(String message) {
        return message.contains("service2") ? "secondRestapi" : "firstRestapi";
    }
}

