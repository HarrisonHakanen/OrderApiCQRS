package com.example.OrderApiUpdate.handler;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OutboxEventHandlerRegistry {

    private final Map<String, OutboxEventHandler> handlers;

    public OutboxEventHandlerRegistry(List<OutboxEventHandler> eventHandlers) {

        this.handlers = eventHandlers.stream()
                .collect(Collectors.toMap(
                        OutboxEventHandler::tipoEvento,
                        Function.identity()
                ));
    }

    public OutboxEventHandler buscar(String tipoEvento) {
        OutboxEventHandler handler = handlers.get(tipoEvento);

        if (handler == null) {
            throw new IllegalArgumentException(
                    "Tipo de evento não suportado: " + tipoEvento
            );
        }

        return handler;
    }
}