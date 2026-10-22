package com.example.OrderApiProcess.service;

import com.example.OrderApiProcess.exception.PedidoNaoEncontradoException;
import com.example.OrderApiProcess.exception.TransicaoStatusInvalidaException;
import com.example.OrderApiProcess.messages.AlterarStatusPedidoCommand;
import com.example.OrderApiProcess.messages.CancelarPedidoCommand;
import com.example.OrderApiProcess.messages.CriarPedidoCommand;
import com.example.OrderApiProcess.model.dto.PedidoItens;
import com.example.OrderApiProcess.model.entity.*;
import com.example.OrderApiProcess.model.enums.PedidoStatus;
import com.example.OrderApiProcess.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PedidoProcessService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final PedidoItemRepository pedidoItemRepository;
    private final PedidoStatusHistoricoRepository historicoRepository;
    private final ComandoProcessadoRepository comandoProcessadoRepository;
    private final OutboxService outboxService;
    private static final Set<String> STATUS_PERMITIDOS =
            Set.of("PROCESSANDO", "FATURADO");

    public PedidoProcessService(
            PedidoRepository pedidoRepository,
            ClienteRepository clienteRepository,
            ProdutoRepository produtoRepository,
            PedidoItemRepository pedidoItemRepository,
            PedidoStatusHistoricoRepository historicoRepository,
            OutboxService outboxService,
            ComandoProcessadoRepository comandoProcessadoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.pedidoItemRepository = pedidoItemRepository;
        this.historicoRepository = historicoRepository;
        this.outboxService = outboxService;
        this.comandoProcessadoRepository = comandoProcessadoRepository;
    }

    @Transactional
    public void processar(CriarPedidoCommand command) {

        boolean novoComando = validarComando(command.commandUUID(),command.orderUUID(), "CRIAR_PEDIDO");
        if (!novoComando) {
            return;
        }

        if (pedidoRepository.existsByUuid(command.orderUUID())) {
            return;
        }

        Cliente cliente = getValidClient(command);

        Map<UUID, Produto> produtosPorUuid = validProducts(command);

        PedidoItens pedidoItens = priceProcess(cliente, command, produtosPorUuid);

        salvarHistorico(pedidoItens.pedido());

        outboxService.registrarPedidoCriado(
                pedidoItens.pedido(),
                pedidoItens.itensParaSalvar());
    }

    public void salvarHistorico(Pedido pedido){
        PedidoStatusHistorico historico = new PedidoStatusHistorico();
        historico.setPedido(pedido);
        historico.setStatusAnterior(null);
        historico.setStatusNovo(PedidoStatus.CRIADO);
        historico.setMotivo("Criação do pedido");

        historicoRepository.save(historico);
    }

    public PedidoItens priceProcess(Cliente cliente, CriarPedidoCommand command, Map<UUID, Produto> produtosPorUuid){
        BigDecimal subtotalPedido = BigDecimal.ZERO;
        BigDecimal descontoPedido = BigDecimal.ZERO;

        List<PedidoItem> itensParaSalvar = new ArrayList<>();

        for (CriarPedidoCommand.Item itemCommand : command.itens()) {

            if (itemCommand.quantidade() == null
                    || itemCommand.quantidade() <= 0) {
                throw new IllegalArgumentException(
                        "A quantidade precisa ser maior que zero.");
            }

            Produto produto =
                    produtosPorUuid.get(itemCommand.produtoUuid());

            BigDecimal precoUnitario = produto.getPrecoAtual();

            BigDecimal descontoItem = BigDecimal.ZERO;

            BigDecimal subtotalItem = precoUnitario
                    .multiply(BigDecimal.valueOf(
                            itemCommand.quantidade()))
                    .subtract(descontoItem);

            subtotalPedido = subtotalPedido.add(subtotalItem);
            descontoPedido = descontoPedido.add(descontoItem);

            PedidoItem item = new PedidoItem();
            item.setProduto(produto);
            item.setQuantidade(itemCommand.quantidade());
            item.setPrecoUnitario(precoUnitario);
            item.setDesconto(descontoItem);
            item.setSubtotal(subtotalItem);

            itensParaSalvar.add(item);
        }

        BigDecimal totalPedido =
                subtotalPedido.subtract(descontoPedido);

        Pedido pedido = new Pedido();
        pedido.setUuid(command.orderUUID());
        pedido.setCliente(cliente);
        pedido.setStatus(PedidoStatus.CRIADO);
        pedido.setSubtotal(subtotalPedido);
        pedido.setDesconto(descontoPedido);
        pedido.setTotal(totalPedido);

        pedido = pedidoRepository.saveAndFlush(pedido);

        for (PedidoItem item : itensParaSalvar) {
            item.setPedido(pedido);
        }

        pedidoItemRepository.saveAll(itensParaSalvar);

        return new PedidoItens(pedido,itensParaSalvar);
    }

    public Map<UUID, Produto> validProducts(CriarPedidoCommand command){
        if (command.itens() == null || command.itens().isEmpty()) {
            throw new IllegalArgumentException(
                    "O pedido precisa conter ao menos um item.");
        }

        Set<UUID> produtoUuids = command.itens().stream()
                .map(CriarPedidoCommand.Item::produtoUuid)
                .collect(Collectors.toSet());

        Map<UUID, Produto> produtosPorUuid = produtoRepository
                .findAllByUuidIn(produtoUuids)
                .stream()
                .collect(Collectors.toMap(
                        Produto::getUuid,
                        Function.identity()));

        for (UUID produtoUuid : produtoUuids) {
            Produto produto = produtosPorUuid.get(produtoUuid);

            if (produto == null) {
                throw new IllegalArgumentException(
                        "Produto não encontrado: " + produtoUuid);
            }

            if (!produto.isAtivo()) {
                throw new IllegalArgumentException(
                        "Produto inativo: " + produtoUuid);
            }
        }

        return produtosPorUuid;
    }


    public Cliente getValidClient(CriarPedidoCommand command){
        // 2. Buscar e validar o cliente.
        Cliente cliente = clienteRepository
                .findByUuid(command.clienteUuid())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Cliente não encontrado: "
                                        + command.clienteUuid()));

        if (!cliente.isAtivo()) {
            throw new IllegalArgumentException(
                    "O cliente está inativo.");
        }

        return cliente;
    }

    @Transactional
    public void cancelarPedido(CancelarPedidoCommand command) {

        boolean novoComando = validarComando(command.commandUUID(),command.orderUUID(), "CANCELAR_PEDIDO");
        if (!novoComando) {
            return;
        }

        Pedido pedido = pedidoRepository.findByUuid(command.orderUUID())
                .orElseThrow(() -> new PedidoNaoEncontradoException(
                        "Pedido não encontrado: " + command.orderUUID()
                ));

        PedidoStatus statusAnterior = pedido.getStatus();

        if (statusAnterior != PedidoStatus.CRIADO && statusAnterior != PedidoStatus.PROCESSANDO) {

            throw new TransicaoStatusInvalidaException(
                    "Não é possível cancelar um pedido com status "
                            + statusAnterior
            );
        }

        pedido.setStatus(PedidoStatus.CANCELADO);

        PedidoStatusHistorico historico = new PedidoStatusHistorico();
        historico.setPedido(pedido);
        historico.setStatusAnterior(statusAnterior);
        historico.setStatusNovo(PedidoStatus.CANCELADO);
        historico.setMotivo("Cancelamento do pedido");

        historicoRepository.save(historico);

        outboxService.cancelarPedido(pedido);
    }


    @Transactional
    public void alterarStatusPedido(AlterarStatusPedidoCommand command) {

        boolean novoComando = validarComando(
                command.commandUUID(),
                command.orderUUID(),
                "ALTERAR_STATUS"
        );

        if (!novoComando) {
            return;
        }

        Pedido pedido = pedidoRepository.findByUuid(command.orderUUID())
                .orElseThrow(() -> new PedidoNaoEncontradoException(
                        "Pedido não encontrado: " + command.orderUUID()
                ));

        PedidoStatus statusAnterior = pedido.getStatus();

        PedidoStatus novoStatus = validarNovoStatus(command,statusAnterior);

        pedido.setStatus(novoStatus);

        pedidoRepository.save(pedido);

        PedidoStatusHistorico historico = new PedidoStatusHistorico();
        historico.setPedido(pedido);
        historico.setStatusAnterior(statusAnterior);
        historico.setStatusNovo(novoStatus);
        historico.setMotivo("Alteração de status do pedido");

        historicoRepository.save(historico);

        outboxService.alterarPedido(pedido);
    }

    public PedidoStatus validarNovoStatus(AlterarStatusPedidoCommand command, PedidoStatus statusAnterior){
        PedidoStatus novoStatus;

        try {
            novoStatus = PedidoStatus.valueOf(command.statusSolicitado());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new TransicaoStatusInvalidaException(
                    "Status solicitado inválido: " + command.statusSolicitado()
            );
        }

        if (statusAnterior == PedidoStatus.CANCELADO
                || statusAnterior == PedidoStatus.FATURADO) {
            throw new TransicaoStatusInvalidaException(
                    "Não é possível alterar um pedido com status " + statusAnterior
            );
        }

        if (novoStatus != PedidoStatus.PROCESSANDO
                && novoStatus != PedidoStatus.FATURADO) {
            throw new TransicaoStatusInvalidaException(
                    "Status de destino não permitido: " + novoStatus
            );
        }

        if (statusAnterior == novoStatus) {
            throw new TransicaoStatusInvalidaException(
                    "O pedido já está no status " + novoStatus
            );
        }

        return novoStatus;
    }


    public boolean validarComando(UUID commandUUID, UUID orderUUID,String tipoComando){
        int registrado = comandoProcessadoRepository.registrarComando(
                commandUUID,
                tipoComando,
                orderUUID
        );

        return registrado == 1;
    }
}
