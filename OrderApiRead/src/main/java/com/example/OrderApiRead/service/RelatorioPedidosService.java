package com.example.OrderApiRead.service;

import com.example.OrderApiRead.models.dto.RelatorioPedidosResponse;
import com.example.OrderApiRead.repository.RelatorioPedidosRepository;
import com.example.OrderApiRead.projections.RelatorioPedidosResumoProjection;
import com.example.OrderApiRead.projections.RelatorioPedidosStatusProjection;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
public class RelatorioPedidosService {

    private final RelatorioPedidosRepository repository;

    public RelatorioPedidosService(
            RelatorioPedidosRepository repository
    ) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public RelatorioPedidosResponse gerarRelatorio(
            LocalDate dataInicial,
            LocalDate dataFinal
    ) {
        if (dataInicial == null || dataFinal == null) {
            throw new IllegalArgumentException(
                    "As datas inicial e final são obrigatórias."
            );
        }

        if (dataInicial.isAfter(dataFinal)) {
            throw new IllegalArgumentException(
                    "A data inicial não pode ser posterior à data final."
            );
        }

        LocalDate diaSeguinte = dataFinal.plusDays(1);

        var inicio = dataInicial
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC);

        var fim = diaSeguinte
                .atStartOfDay()
                .toInstant(ZoneOffset.UTC);

        RelatorioPedidosResumoProjection resumo =
                repository.buscarResumo(inicio, fim);

        List<RelatorioPedidosResponse.ResumoStatusPedido> resumoPorStatus =
                repository.buscarResumoPorStatus(inicio, fim)
                        .stream()
                        .map(this::converterStatus)
                        .toList();

        return new RelatorioPedidosResponse(
                dataInicial,
                dataFinal,
                valorLong(resumo.getTotalPedidos()),
                valorLong(resumo.getPedidosCancelados()),
                valorLong(resumo.getPedidosValidos()),
                valorDecimal(resumo.getValorTotalVendas()),
                valorDecimal(resumo.getTicketMedio()),
                resumoPorStatus
        );
    }

    private RelatorioPedidosResponse.ResumoStatusPedido converterStatus(
            RelatorioPedidosStatusProjection projection
    ) {
        return new RelatorioPedidosResponse.ResumoStatusPedido(
                projection.getStatus(),
                valorLong(projection.getQuantidade()),
                valorDecimal(projection.getValorTotal())
        );
    }

    private long valorLong(Long valor) {
        return valor == null ? 0L : valor;
    }

    private BigDecimal valorDecimal(BigDecimal valor) {
        return valor == null ? BigDecimal.ZERO : valor;
    }
}
