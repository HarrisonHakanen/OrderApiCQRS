package com.example.OrderApiRead.controller;

import com.example.OrderApiRead.models.dto.PedidoResponse;
import com.example.OrderApiRead.models.dto.PedidoStatusHistoricoResponse;
import com.example.OrderApiRead.service.PedidoReadService;

import com.example.OrderApiRead.service.PedidoStatusHistoricoService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class PedidoReadController {

    private final PedidoReadService pedidoReadService;
    private final PedidoStatusHistoricoService historicoService;

    public PedidoReadController(PedidoReadService pedidoReadService, PedidoStatusHistoricoService historicoService) {
        this.pedidoReadService = pedidoReadService;
        this.historicoService = historicoService;
    }

    @GetMapping("/orders/{orderUUID}")
    public ResponseEntity<PedidoResponse> buscarPedido(
            @PathVariable UUID orderUUID
    ) {
        return pedidoReadService.buscarPorUuid(orderUUID)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/orders")
    public ResponseEntity<Page<PedidoResponse>> listarPedidos(
            @RequestParam(required = false) UUID customerUUID,
            @RequestParam(required = false) String status,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant criadoDe,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            Instant criadoAte,
            @PageableDefault(
                    size = 20,
                    sort = "criadoEm",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        if (criadoDe != null && criadoAte != null
                && criadoDe.isAfter(criadoAte)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                pedidoReadService.listar(
                        customerUUID, status, criadoDe, criadoAte, pageable
                )
        );
    }

    @GetMapping("/orders/{orderUUID}/history")
    public List<PedidoStatusHistoricoResponse> buscarHistorico(
            @PathVariable("orderUUID") UUID orderUUID
    ) {
        return historicoService.buscarHistorico(orderUUID);
    }
}
