package com.example.OrderApiInsert.service;

import com.example.OrderApiInsert.models.entity.Produto;
import com.example.OrderApiInsert.repository.ClienteRepository;
import com.example.OrderApiInsert.repository.ProdutoRepository;
import com.example.OrderApiInsert.dto.CriarPedidoRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PedidoReferenciaValidator {

    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoReferenciaValidator(
            ClienteRepository clienteRepository,
            ProdutoRepository produtoRepository) {
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public void validar(CriarPedidoRequest request) {
        UUID clienteUuid = request.clienteUuid();

        if (!clienteRepository.existsByUuid(clienteUuid)) {
            throw new IllegalArgumentException(
                    "Cliente não encontrado: " + clienteUuid
            );
        }

        List<UUID> produtoUuids = request.itens().stream()
                .map(CriarPedidoRequest.ItemPedidoRequest::produtoUuid)
                .distinct()
                .toList();

        Set<UUID> produtosEncontrados = produtoRepository
                .findAllByUuidIn(produtoUuids)
                .stream()
                .map(Produto::getUuid)
                .collect(Collectors.toSet());

        List<UUID> produtosAusentes = produtoUuids.stream()
                .filter(uuid -> !produtosEncontrados.contains(uuid))
                .toList();

        if (!produtosAusentes.isEmpty()) {
            throw new IllegalArgumentException(
                    "Produtos não encontrados: " + produtosAusentes
            );
        }
    }
}
