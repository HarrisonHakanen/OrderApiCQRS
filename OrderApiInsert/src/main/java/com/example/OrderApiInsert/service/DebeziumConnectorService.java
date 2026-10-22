package com.example.OrderApiInsert.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.InputStream;
import java.util.Map;

@Service
public class DebeziumConnectorService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public DebeziumConnectorService(
            @Value("${debezium.connect-url:http://localhost:8083}")
            String connectUrl,
            ObjectMapper objectMapper
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(connectUrl)
                .build();

        this.objectMapper = objectMapper;
    }

    public void registrarConector() throws Exception {
        JsonNode document;

        try (InputStream input = getClass()
                .getResourceAsStream("/register-outbox-connector.json")) {

            if (input == null) {
                throw new IllegalStateException(
                        "Arquivo register-outbox-connector.json não encontrado"
                );
            }

            document = objectMapper.readTree(input);
        }

        Map<String, Object> config = objectMapper.convertValue(
                document.get("config"),
                Map.class
        );

        restClient.put()
                .uri("/connectors/ordersys-outbox-connector/config")
                .body(config)
                .retrieve()
                .toBodilessEntity();
    }
}
