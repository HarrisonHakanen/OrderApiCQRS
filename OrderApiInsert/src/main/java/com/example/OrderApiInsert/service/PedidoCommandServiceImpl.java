package com.example.OrderApiInsert.service;

import com.example.OrderApiInsert.dto.*;
import com.example.OrderApiInsert.messages.CriarPedidoCommand;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class PedidoCommandServiceImpl implements PedidoCommandService {

    private static final Set<String> STATUS_PERMITIDOS =
            Set.of("PROCESSANDO", "FATURADO");
    private static final String TOPIC = "orders.create.requested";
    private static final String topicoAlteracaoStatus = "orders.alter.status.requested";
    private static final String topicoCancelamento = "orders.calcelation.requested";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final PedidoReferenciaValidator pedidoReferenciaValidator;


    public PedidoCommandServiceImpl(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper, PedidoReferenciaValidator pedidoReferenciaValidator) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.pedidoReferenciaValidator = pedidoReferenciaValidator;
    }

    @Override
    public UUID solicitarCriacao(CriarPedidoRequest request) {
        validarRequisicao(request);
        pedidoReferenciaValidator.validar(request);

        UUID orderUUID = UUID.randomUUID();
        UUID commandUUID = UUID.randomUUID();

        CriarPedidoCommand command = new CriarPedidoCommand(
                commandUUID,
                orderUUID,
                request.clienteUuid(),
                request.itens().stream()
                        .map(item -> new CriarPedidoCommand.Item(
                                item.produtoUuid(),
                                item.quantidade()
                        ))
                        .toList(),
                Instant.now()
        );


        try {
            String payload = objectMapper.writeValueAsString(command);
            publicar(TOPIC, orderUUID, payload);

            return orderUUID;

        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Não foi possível serializar o comando do pedido.", e
            );
        }
    }


    @Override
    public void solicitarAlteracaoStatus(
            UUID orderUUID,
            AlterarStatusRequest request
    ) {
        if (request == null || request.status() == null) {
            throw new IllegalArgumentException(
                    "O status solicitado é obrigatório."
            );
        }

        String status = request.status().toString();

        if (!STATUS_PERMITIDOS.contains(status)) {
            throw new IllegalArgumentException(
                    "Status não permitido neste endpoint. "
                            + "Utilize PROCESSANDO ou FATURADO."
            );
        }

        AlterarStatusPedidoCommand comando =
                new AlterarStatusPedidoCommand(
                        UUID.randomUUID(),
                        orderUUID,
                        status,
                        request.motivo(),
                        Instant.now()
                );


        try {
            String payload = objectMapper.writeValueAsString(comando);
            publicar(topicoAlteracaoStatus, orderUUID, payload);

        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Não foi possível serializar o comando do pedido.", e
            );
        }
    }

    @Override
    public void solicitarCancelamento(
            UUID orderUUID,
            CancelarPedidoRequest request
    ) {
        CancelarPedidoCommand comando =
                new CancelarPedidoCommand(
                        UUID.randomUUID(),
                        orderUUID,
                        request == null ? null : request.motivo(),
                        Instant.now()
                );


        try {
            String payload = objectMapper.writeValueAsString(comando);
            publicar(topicoCancelamento, orderUUID, payload);

        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Não foi possível serializar o comando do pedido.", e
            );
        }
    }

    private void validarRequisicao(CriarPedidoRequest request) {
        if (request == null) {
            throw new IllegalArgumentException(
                    "A requisição do pedido é obrigatória."
            );
        }

        if (request.clienteUuid() == null) {
            throw new IllegalArgumentException(
                    "O UUID do cliente é obrigatório."
            );
        }

        if (request.itens() == null || request.itens().isEmpty()) {
            throw new IllegalArgumentException(
                    "O pedido deve conter pelo menos um item."
            );
        }

        for (var item : request.itens()) {
            if (item.produtoUuid() == null) {
                throw new IllegalArgumentException(
                        "O UUID do produto é obrigatório."
                );
            }

            if (item.quantidade() == null ||
                    item.quantidade() <= 0) {
                throw new IllegalArgumentException(
                        "A quantidade deve ser maior que zero."
                );
            }
        }
    }

    private void publicar(
            String topico,
            UUID orderUUID,
            String comando
    ) {
        try {
            kafkaTemplate.send(
                    topico,
                    orderUUID.toString(),
                    comando
            ).get(5, TimeUnit.SECONDS);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Publicação do comando interrompida.", e
            );

        } catch (ExecutionException | TimeoutException e) {
            throw new IllegalStateException(
                    "Não foi possível confirmar a publicação do comando no Kafka.",
                    e
            );
        }
    }

}
