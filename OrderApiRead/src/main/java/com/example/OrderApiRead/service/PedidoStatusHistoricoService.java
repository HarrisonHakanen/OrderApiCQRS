package com.example.OrderApiRead.service;

import com.example.OrderApiRead.exeption.PedidoNaoEncontradoException;
import com.example.OrderApiRead.models.dto.PedidoStatusHistoricoResponse;
import com.example.OrderApiRead.repository.PedidoLeituraRepository;
import com.example.OrderApiRead.repository.PedidoStatusHistoricoLeituraRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PedidoStatusHistoricoService {

    private final PedidoLeituraRepository pedidoRepository;
    private final PedidoStatusHistoricoLeituraRepository historicoRepository;

    public PedidoStatusHistoricoService(
            PedidoLeituraRepository pedidoRepository,
            PedidoStatusHistoricoLeituraRepository historicoRepository
    ) {
        this.pedidoRepository = pedidoRepository;
        this.historicoRepository = historicoRepository;
    }

    @Transactional(readOnly = true)
    public List<PedidoStatusHistoricoResponse> buscarHistorico(
            UUID pedidoUuid
    ) {
        if (!pedidoRepository.findByPedidoUuid(pedidoUuid).isPresent()) {
            throw new PedidoNaoEncontradoException(pedidoUuid);
        }

        return historicoRepository
                .findByPedidoUuidOrderByDataStatusAscIdAsc(pedidoUuid)
                .stream()
                .map(historico -> new PedidoStatusHistoricoResponse(
                        historico.getStatus(),
                        historico.getDataStatus(),
                        historico.getObservacao()
                ))
                .toList();
    }
}
