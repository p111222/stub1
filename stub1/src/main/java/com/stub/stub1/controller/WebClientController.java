package com.stub.stub1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.stub.stub1.service.WebClientService;

import reactor.core.publisher.Mono;

@RestController
public class WebClientController {

    @Autowired
    private WebClientService webClientService;

    @GetMapping("/posts/{id}")
    public Mono<String> getPostById(@PathVariable int id) {
        return webClientService.getPostByIdAsync(id);
    }
}

