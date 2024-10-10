// package com.stub.stub1.service;

// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.ResponseEntity;
// import org.springframework.kafka.annotation.KafkaListener;
// import org.springframework.stereotype.Service;
// import org.springframework.web.client.RestTemplate;

// @Service
// public class Service1 {

//     private final RestTemplate restTemplate = new RestTemplate();

//     @KafkaListener(topics = "service1-topic", groupId = "group_id")
//     public void consumeFromKafka(String message) {
//         try {
//             System.out.println("service1 message: " + message);
//             String path = determinePathFromMessage(message);
//             String gatewayUrl = "http://localhost:8081/" + path;
//             System.out.println("gatewayUriservice1:-" + gatewayUrl);
    
//             HttpHeaders headers = new HttpHeaders();
//             headers.set("Message", message); 
    
//             HttpEntity<String> requestEntity = new HttpEntity<>("", headers);
    
//             ResponseEntity<String> response = restTemplate.postForEntity(gatewayUrl, requestEntity, String.class);
    
//             System.out.println("Response: " + response);
//         } catch (Exception e) {
//             System.err.println("Error processing message: " + e.getMessage());
//             e.printStackTrace();
//         }
//     }
    
//     private String determinePathFromMessage(String message) {
//         return message.contains("service1") ? "firstRestapi" : "secondRestapi";
//     }
// }

// package com.stub.stub1.service;

// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpMethod;
// import org.springframework.kafka.annotation.KafkaListener;
// import org.springframework.stereotype.Service;
// import org.springframework.web.client.RestTemplate;

// @Service
// public class Service1 {

// private final RestTemplate restTemplate = new RestTemplate();

// @KafkaListener(topics = "service1-topic", groupId = "group_id")
// public void consumeFromKafka(String message) {
// try {
// System.out.println("Service1 received message: " + message);
// String path = determinePathFromMessage(message);
// String gatewayUrl = "http://localhost:8081/" + path;
// System.out.println("Gateway URL for Service1: " + gatewayUrl);

// HttpHeaders headers = new HttpHeaders();
// headers.set("Source", "service1");

// HttpEntity<String> requestEntity = new HttpEntity<>(headers);

// String response = restTemplate.exchange(gatewayUrl, HttpMethod.GET,
// requestEntity, String.class).getBody();
// System.out.println("Response: " + response);
// } catch (Exception e) {
// System.err.println("Error processing message: " + e.getMessage());
// e.printStackTrace();
// }
// }

// private String determinePathFromMessage(String message) {
// return message.contains("service1") ? "firstRestapi" : "secondRestapi";
// }
// }

// package com.stub.stub1.service;

// import org.springframework.kafka.annotation.KafkaListener;
// import org.springframework.stereotype.Service;

// @Service
// public class Service1 {

//     private final GatewayRequestService gatewayRequestService;

//     public Service1(GatewayRequestService gatewayRequestService) {
//         this.gatewayRequestService = gatewayRequestService;
//     }

//     @KafkaListener(topics = "service1-topic", groupId = "group_id")
//     public void consumeFromKafka(String message) {
//         System.out.println("Kafka message consumed: " + message);
//         gatewayRequestService.forwardMessageToGateway(message);
//     }
// }


package com.stub.stub1.service;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import io.opentelemetry.api.common.AttributeKey; // Ensure this is imported
import io.opentelemetry.api.trace.StatusCode; // Import this for StatusCode

@Service
public class Service1 {

    private final GatewayRequestService gatewayRequestService;
    private final Tracer tracer = GlobalOpenTelemetry.getTracer("service1-tracer");

    public Service1(GatewayRequestService gatewayRequestService) {
        this.gatewayRequestService = gatewayRequestService;
    }

    @KafkaListener(topics = "service1-topic", groupId = "group_id")
    public void consumeFromKafka(String message) {
        Span span = tracer.spanBuilder("Kafka message processing")
                .setSpanKind(SpanKind.CONSUMER) 
                .startSpan();

        try (Scope scope = span.makeCurrent()) {
            System.out.println("Kafka message consumed: " + message);

            gatewayRequestService.forwardMessageToGateway(message);

            span.setAttribute("kafka.message", message); // Setting attribute
            span.setStatus(StatusCode.OK); // Set status to OK for successful processing
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR); // Set status to ERROR in case of exception
            throw e;
        } finally {
            span.end();
        }
    }
}
