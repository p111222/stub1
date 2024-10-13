// package com.stub.stub1.service;

// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;
// import org.springframework.web.client.RestTemplate;

// @Service
// public class GatewayRequestService {

//     private final RestTemplate restTemplate = new RestTemplate(); 

//     public void forwardMessageToGateway(String message) {
//         try {
//             String path = determinePathFromMessage(message);
//             String gatewayUrl = "http://localhost:8081/" + path; 
            
//             System.out.println("Forwarding message to gateway at: " + gatewayUrl);

//             HttpHeaders headers = new HttpHeaders();
//             // headers.set("Source", "RestTemplate"); 
//             headers.set("Message", message);

//             HttpEntity<String> requestEntity = new HttpEntity<>(message, headers);

//             ResponseEntity<String> response = restTemplate.postForEntity(gatewayUrl, requestEntity, String.class);

//             System.out.println("Response from Gateway: " + response.getBody());

//         } catch (Exception e) {
//             System.err.println("Error forwarding message to gateway: " + e.getMessage());
//             e.printStackTrace();
//         }
//     }

//     private String determinePathFromMessage(String message) {
//         return message.contains("service1") ? "firstRestapi" : "secondRestapi";
//     }
// }


package com.stub.stub1.service;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import io.opentelemetry.api.common.AttributeKey; // Ensure this is imported
import io.opentelemetry.api.trace.StatusCode; // Import this for StatusCode

@Service
public class GatewayRequestService {

    private final RestTemplate restTemplate = new RestTemplate();

    private final Tracer tracer = GlobalOpenTelemetry.getTracer("gateway-request-service");

    public void forwardMessageToGateway(String message) {
        Span span = tracer.spanBuilder("Forward message to gateway")
                .setSpanKind(SpanKind.CLIENT)  
                .startSpan();

        try (Scope scope = span.makeCurrent()) {
            String path = determinePathFromMessage(message);
            String gatewayUrl = "http://3.108.54.64:8347/proxyapi/" + path;

            span.setAttribute("http.url", gatewayUrl);
            span.setAttribute("http.method", "POST");

            System.out.println("Forwarding message to gateway at: " + gatewayUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Message", message);

            HttpEntity<String> requestEntity = new HttpEntity<>(message, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(gatewayUrl, requestEntity, String.class);

            span.setAttribute("http.status_code", response.getStatusCode().value());

            System.out.println("Response from Gateway: " + response.getBody());

        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR); 

            span.setAttribute(AttributeKey.booleanKey("error"), true);

            System.err.println("Error forwarding message to gateway: " + e.getMessage());
            e.printStackTrace();
        } finally {
            span.end();
        }
    }

    private String determinePathFromMessage(String message) {
        return message.contains("service1") ? "firstRestapi" : "secondRestapi";
    }
}
