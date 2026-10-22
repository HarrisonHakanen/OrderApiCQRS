package com.example.OrderApiProcess.consumer;

import com.example.OrderApiProcess.messages.AlterarStatusPedidoCommand;
import com.example.OrderApiProcess.messages.CancelarPedidoCommand;
import com.example.OrderApiProcess.messages.CriarPedidoCommand;
import com.example.OrderApiProcess.service.PedidoProcessService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PedidoConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(PedidoConsumer.class);

    private final ObjectMapper objectMapper;
    private final PedidoProcessService pedidoProcessService;

    public PedidoConsumer(
            ObjectMapper objectMapper,
            PedidoProcessService pedidoProcessService) {
        this.objectMapper = objectMapper;
        this.pedidoProcessService = pedidoProcessService;
    }

    @KafkaListener(
            topics = "orders.create.requested",
            groupId = "orders-process-group"
    )
    public void consumirPedido(String payload)
            throws JsonProcessingException {

        CriarPedidoCommand command =
                objectMapper.readValue(
                        payload,
                        CriarPedidoCommand.class
                );

        pedidoProcessService.processar(command);

        log.info(
                "Comando recebido: commandUUID={}, orderUUID={}, clienteUuid={}",
                command.commandUUID(),
                command.orderUUID(),
                command.clienteUuid()
        );

        log.info(
                "Pedido recebido com {} itens",
                command.itens().size()
        );
    }

    @KafkaListener(
            topics = "orders.calcelation.requested",
            groupId = "orders-process-group"
    )
    public void cancelarPedido(String payload)
            throws JsonProcessingException {

        CancelarPedidoCommand command =
                objectMapper.readValue(
                        payload,
                        CancelarPedidoCommand.class
                );

        pedidoProcessService.cancelarPedido(command);

        log.info(
                "Comando recebido: commandUUID={}, orderUUID={}",
                command.commandUUID(),
                command.orderUUID()
        );
    }


    @KafkaListener(
            topics = "orders.alter.status.requested",
            groupId = "orders-process-group"
    )
    public void alterarStatusPedido(String payload)
            throws JsonProcessingException {

        AlterarStatusPedidoCommand command =
                objectMapper.readValue(
                        payload,
                        AlterarStatusPedidoCommand.class
                );

        pedidoProcessService.alterarStatusPedido(command);

        log.info(
                "Comando recebido: commandUUID={}, orderUUID={}",
                command.commandUUID(),
                command.orderUUID()
        );
    }
}
