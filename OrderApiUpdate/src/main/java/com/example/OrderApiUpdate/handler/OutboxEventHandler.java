package com.example.OrderApiUpdate.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

public interface OutboxEventHandler {

    String tipoEvento();

    void processar(JsonNode payload)
            throws JsonProcessingException;
}
