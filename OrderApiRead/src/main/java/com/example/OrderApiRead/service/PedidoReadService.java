package com.example.OrderApiRead.service;

import com.example.OrderApiRead.models.dto.PedidoItemResponse;
import com.example.OrderApiRead.models.dto.PedidoResponse;
import com.example.OrderApiRead.models.entity.PedidoItemLeitura;
import com.example.OrderApiRead.models.entity.PedidoLeitura;
import com.example.OrderApiRead.repository.PedidoItemLeituraRepository;
import com.example.OrderApiRead.repository.PedidoLeituraRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PedidoReadService {

    private final PedidoLeituraRepository pedidoRepository;
    private final PedidoItemLeituraRepository itemRepository;

    public PedidoReadService(
            PedidoLeituraRepository pedidoRepository,
            PedidoItemLeituraRepository itemRepository
    ) {
        this.pedidoRepository = pedidoRepository;
        this.itemRepository = itemRepository;
    }

    public Optional<PedidoResponse> buscarPorUuid(UUID pedidoUuid) {
        return pedidoRepository.findByPedidoUuid(pedidoUuid)
                .map(pedido -> {
                    List<PedidoItemLeitura> itens =
                            itemRepository.findByPedidoUuid(pedidoUuid);

                    return converter(pedido, itens);
                });
    }

    public Page<PedidoResponse> listar(
            UUID clienteUuid,
            String status,
            Instant criadoDe,
            Instant criadoAte,
            Pageable pageable
    ) {
        Specification<PedidoLeitura> spec = (root, query, cb) -> cb.conjunction();

        if (clienteUuid != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("clienteUuid"), clienteUuid));
        }

        if (status != null && !status.isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("status"), status));
        }

        if (criadoDe != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(
                            root.get("criadoEm"), criadoDe));
        }

        if (criadoAte != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(
                            root.get("criadoEm"), criadoAte));
        }

        Page<PedidoLeitura> pagina =
                pedidoRepository.findAll(spec, pageable);

        return converterPagina(pagina);
    }

    public Page<PedidoResponse> listarPorCliente(
            UUID clienteUuid,
            Pageable pageable
    ) {
        Specification<PedidoLeitura> spec =
                (root, query, cb) ->
                        cb.equal(root.get("clienteUuid"), clienteUuid);

        Page<PedidoLeitura> pagina =
                pedidoRepository.findAll(spec, pageable);

        return converterPagina(pagina);
    }

    private Page<PedidoResponse> converterPagina(
            Page<PedidoLeitura> pagina
    ) {
        List<PedidoLeitura> pedidos = pagina.getContent();

        if (pedidos.isEmpty()) {
            return new PageImpl<>(
                    List.of(), pagina.getPageable(), pagina.getTotalElements()
            );
        }

        List<UUID> uuids = pedidos.stream()
                .map(PedidoLeitura::getPedidoUuid)
                .toList();

        Map<UUID, List<PedidoItemLeitura>> itensPorPedido =
                itemRepository.findByPedidoUuidIn(uuids).stream()
                        .collect(Collectors.groupingBy(
                                PedidoItemLeitura::getPedidoUuid
                        ));

        List<PedidoResponse> respostas = pedidos.stream()
                .map(pedido -> converter(
                        pedido,
                        itensPorPedido.getOrDefault(
                                pedido.getPedidoUuid(), List.of()
                        )
                ))
                .toList();

        return new PageImpl<>(
                respostas, pagina.getPageable(), pagina.getTotalElements()
        );
    }

    private PedidoResponse converter(
            PedidoLeitura pedido,
            List<PedidoItemLeitura> itens
    ) {
        List<PedidoItemResponse> itensResponse = itens.stream()
                .map(item -> new PedidoItemResponse(
                        item.getProdutoUuid(),
                        item.getQuantidade(),
                        item.getPrecoUnitario(),
                        item.getDesconto(),
                        item.getSubtotal()
                ))
                .toList();

        return new PedidoResponse(
                pedido.getPedidoUuid(),
                pedido.getClienteUuid(),
                pedido.getStatus(),
                pedido.getSubtotal(),
                pedido.getDesconto(),
                pedido.getTotal(),
                pedido.getCriadoEm(),
                pedido.getAtualizadoEm(),
                itensResponse
        );
    }
}
