package com.example.OrderApiUpdate.service;

import com.example.OrderApiUpdate.models.dto.PedidoCanceladoEvent;
import com.example.OrderApiUpdate.models.dto.PedidoCriadoEvent;
import com.example.OrderApiUpdate.models.entity.EventoProcessado;
import com.example.OrderApiUpdate.models.entity.PedidoItemLeitura;
import com.example.OrderApiUpdate.models.entity.PedidoLeitura;
import com.example.OrderApiUpdate.models.entity.PedidoStatusHistoricoLeitura;
import com.example.OrderApiUpdate.models.enums.PedidoStatus;
import com.example.OrderApiUpdate.repository.EventoProcessadoRepository;
import com.example.OrderApiUpdate.repository.PedidoItemLeituraRepository;
import com.example.OrderApiUpdate.repository.PedidoLeituraRepository;

import com.example.OrderApiUpdate.repository.PedidoStatusHistoricoLeituraRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class PedidoReadModelService {

    private static final String EVENTO_PEDIDO_CRIADO = "PEDIDO_CRIADO";
    private static final String EVENTO_PEDIDO_CANCELADO = "PEDIDO_CANCELADO";
    private static final String EVENTO_PEDIDO_ALTERADO = "PEDIDO_ALTERADO";

    private final PedidoLeituraRepository pedidoRepository;
    private final PedidoItemLeituraRepository itemRepository;
    private final EventoProcessadoRepository eventoRepository;
    private final PedidoStatusHistoricoLeituraRepository pedidoStatusHistoricoLeituraRepository;

    public PedidoReadModelService(
            PedidoLeituraRepository pedidoRepository,
            PedidoItemLeituraRepository itemRepository,
            EventoProcessadoRepository eventoRepository,
            PedidoStatusHistoricoLeituraRepository pedidoStatusHistoricoLeituraRepository
    ) {
        this.pedidoRepository = pedidoRepository;
        this.itemRepository = itemRepository;
        this.eventoRepository = eventoRepository;
        this.pedidoStatusHistoricoLeituraRepository = pedidoStatusHistoricoLeituraRepository;

    }

    @Transactional
    public void processarPedidoCriado(PedidoCriadoEvent evento) {
        validarEvento(evento);

        UUID pedidoUuid = evento.pedidoUuid();

        if (eventoRepository.existsByTipoEventoAndChaveIdempotencia(
                EVENTO_PEDIDO_CRIADO, pedidoUuid)) {
            return;
        }

        if (pedidoRepository.existsByPedidoUuid(pedidoUuid)) {
            throw new IllegalStateException(
                    "Pedido já existe sem registro de evento processado: "
                            + pedidoUuid
            );
        }

        PedidoLeitura pedido = new PedidoLeitura(
                evento.pedidoUuid(),
                evento.clienteUuid(),
                evento.status(),
                evento.subtotal(),
                evento.desconto(),
                evento.total(),
                evento.criadoEm()
        );

        pedidoRepository.save(pedido);

        for (PedidoCriadoEvent.ItemPedidoEvent item : evento.itens()) {
            PedidoItemLeitura entidade = new PedidoItemLeitura(
                    evento.pedidoUuid(),
                    item.produtoUuid(),
                    item.quantidade(),
                    item.precoUnitario(),
                    item.desconto(),
                    item.subtotal()
            );

            itemRepository.save(entidade);
        }


        PedidoStatusHistoricoLeitura pedidoStatusHistorico = new PedidoStatusHistoricoLeitura(
                pedidoUuid,
                PedidoStatus.CRIADO.toString(),
                evento.criadoEm(),
                ""
        );

        pedidoStatusHistoricoLeituraRepository.save(pedidoStatusHistorico);



        eventoRepository.save(
                new EventoProcessado(pedidoUuid, EVENTO_PEDIDO_CRIADO)
        );
    }

    @Transactional
    public void processarPedidoCancelado(PedidoCanceladoEvent evento) {

        UUID pedidoUuid = evento.pedidoUuid();

        Optional<PedidoLeitura> pedidoOpt = pedidoRepository.findByPedidoUuid(pedidoUuid);

        PedidoLeitura pedido = pedidoOpt.get();
        pedido.setStatus(evento.status().toString());

        pedidoRepository.save(pedido);


        PedidoStatusHistoricoLeitura pedidoStatusHistorico = new PedidoStatusHistoricoLeitura(
                pedidoUuid,
                PedidoStatus.CANCELADO.toString(),
                evento.criadoEm(),
                ""
        );

        pedidoStatusHistoricoLeituraRepository.save(pedidoStatusHistorico);



        eventoRepository.save(
                new EventoProcessado(pedidoUuid, EVENTO_PEDIDO_CANCELADO)
        );
    }

    @Transactional
    public void processarPedidoAlterado(PedidoCanceladoEvent evento) {

        UUID pedidoUuid = evento.pedidoUuid();

        Optional<PedidoLeitura> pedidoOpt = pedidoRepository.findByPedidoUuid(pedidoUuid);

        PedidoLeitura pedido = pedidoOpt.get();
        pedido.setStatus(evento.status().toString());

        pedidoRepository.save(pedido);


        PedidoStatusHistoricoLeitura pedidoStatusHistorico = new PedidoStatusHistoricoLeitura(
                pedidoUuid,
                evento.status().toString(),
                evento.criadoEm(),
                ""
        );

        pedidoStatusHistoricoLeituraRepository.save(pedidoStatusHistorico);



        eventoRepository.save(
                new EventoProcessado(pedidoUuid, EVENTO_PEDIDO_ALTERADO)
        );
    }

    private void validarEvento(PedidoCriadoEvent evento) {
        if (evento == null
                || !EVENTO_PEDIDO_CRIADO.equals(evento.evento())) {
            throw new IllegalArgumentException(
                    "Tipo de evento inválido ou não suportado"
            );
        }

        if (evento.pedidoUuid() == null
                || evento.clienteUuid() == null
                || evento.criadoEm() == null
                || evento.status() == null
                || evento.subtotal() == null
                || evento.desconto() == null
                || evento.total() == null
                || evento.itens() == null
                || evento.itens().isEmpty()) {
            throw new IllegalArgumentException(
                    "Evento de criação de pedido incompleto"
            );
        }

        for (PedidoCriadoEvent.ItemPedidoEvent item : evento.itens()) {
            if (item.produtoUuid() == null
                    || item.quantidade() == null
                    || item.quantidade() <= 0
                    || item.precoUnitario() == null
                    || item.desconto() == null
                    || item.subtotal() == null) {
                throw new IllegalArgumentException(
                        "Evento contém um item inválido"
                );
            }
        }
    }
}
