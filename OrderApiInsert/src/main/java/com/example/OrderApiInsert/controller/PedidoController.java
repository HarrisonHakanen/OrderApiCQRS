package com.example.OrderApiInsert.controller;

import com.example.OrderApiInsert.dto.AlterarStatusRequest;
import com.example.OrderApiInsert.dto.CancelarPedidoRequest;
import com.example.OrderApiInsert.dto.ComandoAceitoResponse;
import com.example.OrderApiInsert.dto.CriarPedidoRequest;
import com.example.OrderApiInsert.service.PedidoCommandService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class PedidoController {

    private final PedidoCommandService pedidoCommandService;

    public PedidoController(PedidoCommandService pedidoCommandService) {
        this.pedidoCommandService = pedidoCommandService;
    }

    @PostMapping
    public ResponseEntity<ComandoAceitoResponse> criarPedido(
            @Valid @RequestBody CriarPedidoRequest request) {

        UUID orderUUID = pedidoCommandService.solicitarCriacao(request);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(new ComandoAceitoResponse(
                        orderUUID,
                        "RECEBIDA",
                        "Pedido recebido para processamento."
                ));
    }

    @PatchMapping("/{orderUUID}/status")
    public ResponseEntity<ComandoAceitoResponse> alterarStatus(
            @PathVariable UUID orderUUID,
            @Valid @RequestBody AlterarStatusRequest request) {

        pedidoCommandService.solicitarAlteracaoStatus(
                orderUUID,
                request
        );

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(new ComandoAceitoResponse(
                        orderUUID,
                        "RECEBIDA",
                        "Solicitação de alteração de status recebida."
                ));
    }

    @PostMapping("/{orderUUID}/cancellation")
    public ResponseEntity<ComandoAceitoResponse> cancelarPedido(
            @PathVariable UUID orderUUID,
            @Valid @RequestBody CancelarPedidoRequest request) {

        pedidoCommandService.solicitarCancelamento(
                orderUUID,
                request
        );

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(new ComandoAceitoResponse(
                        orderUUID,
                        "RECEBIDA",
                        "Solicitação de cancelamento recebida."
                ));
    }
}
